package cz.kudladev.routes

import cz.kudladev.data.models.ChargeRecord
import cz.kudladev.data.models.ChargeRecordInsert
import cz.kudladev.domain.repository.ChargeRecordsDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.chargerrecords(chargeRecordsDao: ChargeRecordsDao){
    route("/chargers"){
        get("records") {
            call.respond(chargeRecordsDao.getAllChargeRecords())
        }
        get("{id}/records") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respondText("Please insert right form of ID (Int), starting from 1")
                val chargerRecords = chargeRecordsDao.getChargeRecordById(id)
                if (chargerRecords != null) {
                    call.respond(chargerRecords)
                } else {
                    call.respondText(text = "Charge Record with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        post("records") {
            try {
                val chargeRecord = call.receive<ChargeRecordInsert>()
                val id = chargeRecordsDao.createChargeRecord(chargeRecord)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        put("records") {
            try {
                val chargeRecord = call.receive<ChargeRecord>()
                val updatedChargeRecord = chargeRecordsDao.updateChargeRecord(chargeRecord)
                if (updatedChargeRecord != null) {
                    call.respond(HttpStatusCode.OK, updatedChargeRecord)
                } else {
                    call.respondText(text = "Charge Record with ID ${chargeRecord.idChargeRecord} not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        delete("{id}/records") {
            try {
                val id = call.parameters["id"]?.toInt() ?: return@delete call.respondText("Please insert right form of ID (Int), starting from 1")
                val deletedChargeRecord = chargeRecordsDao.deleteChargeRecord(id)
                if (deletedChargeRecord != null) {
                    call.respond(HttpStatusCode.OK, deletedChargeRecord)
                } else {
                    call.respondText(text = "Charge Record with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception) {
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }
        get("records/notended") {
            try {
                call.respond(chargeRecordsDao.getNotEndedChargeRecordsWithTracking())
            } catch (e: Exception) {
                call.respondText(text = "Error: $e", status = HttpStatusCode.BadRequest)
            }
        }
    }
}