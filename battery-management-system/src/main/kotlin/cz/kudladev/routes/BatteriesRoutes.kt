package cz.kudladev.routes

import cz.kudladev.data.models.Battery
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
            call.respond(batteriesDao.getBatteryById(call.parameters["id"]?.toInt() ?: 0) ?: HttpStatusCode.NotFound)
        }
        post {
            val battery = call.receive<Battery>()
            val id = batteriesDao.createBattery(battery)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }
        put {
            val battery = call.receive<Battery>()
            val id = batteriesDao.updateBattery(battery)
            call.respond(HttpStatusCode.OK, mapOf("id" to id))
        }
        delete("{id}") {
            val id = call.parameters["id"]?.toInt() ?: 0
            batteriesDao.deleteBattery(id)
            call.respond(HttpStatusCode.OK)
        }
    }
}