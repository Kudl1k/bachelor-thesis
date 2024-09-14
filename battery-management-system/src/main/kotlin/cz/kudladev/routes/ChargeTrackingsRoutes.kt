package cz.kudladev.routes

import DatabaseBuilder
import cz.kudladev.data.models.ChargeRecordInsert
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.data.models.StartChargeTracking
import cz.kudladev.domain.repository.*
import cz.kudladev.system.*
import cz.kudladev.util.ResultRowParser
import io.ktor.http.*
import io.ktor.network.sockets.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.consumeEach
import org.jetbrains.exposed.dao.EntityChange
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.toEntity
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.postgresql.PGNotification
import org.postgresql.jdbc.PgConnection
import java.sql.DriverManager
import java.sql.Timestamp

val clients = mutableSetOf<DefaultWebSocketServerSession>()

fun Route.chargetrackings(
    chargeTrackingDao: ChargeTrackingDao,
    chargerRecordsDao: ChargeRecordsDao,
    chargersDao: ChargersDao,
    batteriesDao: BatteriesDao,
    cellDao: CellDao
){



    route("/chargers") {
        get("tracking") {
            try {
                call.respond(chargeTrackingDao.getAllChargeTracking())
            } catch (e: Exception) {
                call.respondText(text = "Error: $e", status = HttpStatusCode.BadRequest)
            }
        }
        get("{id}/tracking") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val chargeTracking = chargeTrackingDao.getChargeTrackingById(id)
                if (chargeTracking != null) {
                    call.respond(chargeTracking)
                } else {
                    call.respondText(text = "Charge Tracking with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(
                    text = "Please insert right form of ID (Int), starting from 1",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        post("tracking") {
            try {
                val chargeTracking = call.receive<ChargeTrackingID>()
                val id = chargeTrackingDao.createChargeTracking(chargeTracking)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        post("{id}/tracking/start") {
            val id = call.parameters["id"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest)
            val charger = chargersDao.getChargerById(id) ?: return@post call.respond(HttpStatusCode.BadRequest)
            val batteries = call.receive<StartChargeTracking>()
            if (job == null || job?.isCancelled == true) {
                isRunning = true
                openPort = openPort(
                    portName = charger.tty,
                    baudRate = charger.baudRate,
                    dataBits = charger.dataBits,
                    stopBits = charger.stopBits,
                    parity = charger.parity,
                    rts = charger.rts,
                    dtr = charger.dtr
                )
                if (openPort?.isOpened == false) {
                    call.respondText("Port is not opened", status = HttpStatusCode.BadRequest)
                    return@post
                }
                job = CoroutineScope(Dispatchers.Default).launch {
                    startTracking(
                        charger,
                        batteries.batteries,
                        chargeTrackingDao,
                        chargerRecordsDao,
                        batteriesDao,
                        cellDao
                    )
                }
                call.respondText("Process started", status = HttpStatusCode.OK)
            } else {
                call.respondText("Process is already running", status = HttpStatusCode.BadRequest)
            }
        }
        get("{id}/tracking/stop") {
            if (job != null && job?.isActive == true) {
                stopTracking()
                call.respondText("Process stopped", status = HttpStatusCode.OK)
            } else {
                call.respondText("No process running", status = HttpStatusCode.BadRequest)
            }
        }
        webSocket("{id}/tracking/last") {
            val subscription = DatabaseBuilder.broadcastChannel.openSubscription()
            try {
                for (message in subscription) {
                    send(message)
                }
            } catch (e: ClosedReceiveChannelException) {
                println("Channel closed")
            } finally {
                subscription.cancel()
            }
        }
    }
}

