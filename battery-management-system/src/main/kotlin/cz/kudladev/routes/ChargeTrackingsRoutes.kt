package cz.kudladev.routes

import cz.kudladev.data.models.ChargeTrackingID
import cz.kudladev.domain.repository.ChargeTrackingDao
import cz.kudladev.system.isRunning
import cz.kudladev.system.job
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Route.chargetrackings(chargeTrackingDao: ChargeTrackingDao){
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
        delete("{id}/tracking") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                chargeTrackingDao.deleteChargeTracking(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(
                    text = "Please insert right form of ID (Int), starting from 1",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        get("{id}/tracking/start") {
            if (job == null || job?.isCancelled == true) {
                isRunning = true
                job = CoroutineScope(Dispatchers.Default).launch {
                    cz.kudladev.system.run()
                }
                call.respondText("Process started", status = HttpStatusCode.OK)
            } else {
                call.respondText("Process is already running", status = HttpStatusCode.BadRequest)
            }
        }
        get("{id}/tracking/stop") {
            if (job != null && job?.isActive == true) {
                isRunning = false
                job?.cancel()
                call.respondText("Process stopped", status = HttpStatusCode.OK)
            } else {
                call.respondText("No process running", status = HttpStatusCode.BadRequest)
            }
        }
    }
}