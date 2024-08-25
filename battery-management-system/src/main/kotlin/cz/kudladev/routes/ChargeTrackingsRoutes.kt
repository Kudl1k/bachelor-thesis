package cz.kudladev.routes

import cz.kudladev.data.ChargeTracking
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.models.ChargeRecordInsert
import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.data.models.StartChargeTracking
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.system.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import org.jetbrains.exposed.dao.EntityChange
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.chargetrackings(
    chargeTrackingDao: ChargeTrackingDao,
    chargerRecordsDao: ChargeRecordsDao,
    chargersDao: ChargersDao
){

    val clients = mutableSetOf<DefaultWebSocketSession>()

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
        put("tracking") {
            try {
                val chargeTracking = call.receive<ChargeTrackingID>()
                chargeTrackingDao.updateChargeTracking(chargeTracking)
                call.respond(HttpStatusCode.OK)
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
                job = CoroutineScope(Dispatchers.Default).launch {
                    startTracking(
                        charger,
                        batteries.batteries,
                        chargeTrackingDao,
                        chargerRecordsDao
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
            val id = call.parameters["id"]?.toInt() ?: return@webSocket close(CloseReason(CloseReason.Codes.CANNOT_ACCEPT, "Invalid ID"))
            clients.add(this)
            var action: (EntityChange) -> Unit
            try {
                send(Frame.Text("Connection established."))

                // Use coroutine scope to launch the action
                action = EntityHook.subscribe { entity ->
                    if (entity.entityClass == ChargeTracking::class) {
                        launch {
                            clients.forEach { client ->
                                client.send(Frame.Text("Updated ChargeTracking: $entity"))
                            }
                        }
                    }
                }

                for (frame in incoming) {
                    // Handle incoming frames if necessary
                }
            } finally {
                clients.remove(this)

            }
        }
    }


}