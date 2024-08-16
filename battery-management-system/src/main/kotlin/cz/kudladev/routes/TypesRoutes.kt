package cz.kudladev.routes

import cz.kudladev.data.models.Type
import cz.kudladev.domain.repository.TypesDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.Timestamp
import java.time.Instant

fun Route.types(typesDAO: TypesDao){
    route("/types"){
        get {
            call.respond(typesDAO.getAllTypes())
        }

        get("{id}"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val type = typesDAO.getTypeById(id)
                if (type != null){
                    call.respond(type)
                } else {
                    call.respondText(text = "Type with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }

        get("{id}/batteries"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@get call.respond(HttpStatusCode.BadRequest)
                val typeWithBatteries = typesDAO.getTypeByIdWithBatteries(id)
                if (typeWithBatteries != null){
                    call.respond(typeWithBatteries)
                } else {
                    call.respondText(text = "Type with ID $id not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception){
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }

        post {
            try {
                val type = call.receive<Type>()
                val id = typesDAO.insertType(type)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception){
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }

        put("{id}"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@put call.respond(HttpStatusCode.BadRequest)
                val type = call.receive<Type>()
                val updatedType = typesDAO.updateType(type.copy(id = id))
                if (updatedType != null){
                    call.respond(HttpStatusCode.OK, mapOf("id" to id))
                } else {
                    call.respondText(text = "Type with ID $id not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }

        delete("{id}"){
            try {
                val id = call.parameters["id"]?.toInt() ?: return@delete call.respond(HttpStatusCode.BadRequest)
                typesDAO.deleteType(id)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of ID (Int), starting from 1", status = HttpStatusCode.BadRequest)
            }
        }

    }



}