package cz.kudladev.routes

import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerInsert
import cz.kudladev.data.models.ChargerWithTypesAndSizes
import cz.kudladev.data.models.SearchCharger
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.system.getAvailablePorts
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import jssc.SerialPort
import jssc.SerialPortList

fun Route.chargers(chargersDao: ChargersDao){
    route("/chargers"){
        get {
            call.respond(chargersDao.getAllChargers())
        }
        get("{id}"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val charger = chargersDao.getChargerById(id)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        post("/search"){
            try {
                val searchCharger = call.receive<SearchCharger>()
                call.respond(chargersDao.getChargerByTypesAndSizes(searchCharger))
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        post {
            try {
                val charger = call.receive<ChargerInsert>()
                val id = chargersDao.createCharger(charger)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception) {
                println(e)
                call.respondText(text = "Please fill all fields $e", status = HttpStatusCode.BadRequest)
            }
        }
        put{
            try {
                val charger = call.receive<Charger>()
                chargersDao.updateCharger(charger)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        delete("{id}"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                chargersDao.deleteCharger(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }

        post("{chargerId}/type/{typeId}"){
            try {
                val chargerId = call.parameters["chargerId"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val typeId = call.parameters["typeId"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val charger = chargersDao.addTypeToCharger(chargerId, typeId)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $chargerId not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        delete("{chargerId}/type/{typeId}"){
            try {
                val chargerId = call.parameters["chargerId"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val typeId = call.parameters["typeId"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val charger = chargersDao.removeTypeFromCharger(chargerId, typeId)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $chargerId not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        post("{chargerId}/size/{sizeName}") {
            try {
                val chargerId = call.parameters["chargerId"]?.toInt() ?: return@post call.respond(HttpStatusCode.BadRequest)
                val sizeName = call.parameters["sizeName"] ?: return@post call.respond(HttpStatusCode.BadRequest)
                val charger = chargersDao.addSizeToCharger(chargerId, sizeName)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $chargerId not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(
                    text = "Please insert right form of ID (Int), starting from 1",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        delete("{chargerId}/size/{sizeName}") {
            try {
                val chargerId = call.parameters["chargerId"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val sizeName = call.parameters["sizeName"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                val charger = chargersDao.removeSizeFromCharger(chargerId, sizeName)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $chargerId not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(
                    text = "Please insert right form of ID (Int), starting from 1",
                    status = HttpStatusCode.BadRequest
                )
            }
        }
        get("/ports"){
            call.respond(getAvailablePorts())
        }
        put("{chargerId}/port"){
            try {
                val chargerId = call.parameters["chargerId"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val port = call.receive<String>()
                val charger = chargersDao.updatePort(chargerId, port)
                if (charger != null) {
                    call.respond(charger)
                } else {
                    call.respondText(text = "Charger with ID $chargerId not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(
                    text = "Please insert right form of ID (Int), starting from 1",
                    status = HttpStatusCode.BadRequest
                )
            }

        }
        get("/parsers") {
            call.respond(chargersDao.getParsers())
        }
    }
}