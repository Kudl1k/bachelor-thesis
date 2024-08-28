package cz.kudladev.routes

import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.BatteryInsert
import cz.kudladev.domain.repository.BatteriesDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.batteries(batteriesDao: BatteriesDao) {
    route("/batteries") {
        get {
            call.respond(batteriesDao.getAllBatteries())
        }
        get("{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val battery = batteriesDao.getBatteryById(id)
                if (battery != null) {
                    call.respond(battery)
                } else {
                    call.respondText(text = "Battery with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        get("{id}/info") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val batteryInfo = batteriesDao.getBatteryInfo(id)
                if (batteryInfo != null) {
                    println("not null")
                    call.respond(batteryInfo)
                } else {
                    println("null")
                    call.respondText(text = "Battery with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        post {
            try {
                val battery = call.receive<BatteryInsert>()
                val id = batteriesDao.createBattery(battery) ?: throw Exception()
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception) {
                println(e)
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        put {
            try {
                val battery = call.receive<Battery>()
                batteriesDao.updateBattery(battery)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        delete("{id}") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                batteriesDao.deleteBattery(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
    }
}