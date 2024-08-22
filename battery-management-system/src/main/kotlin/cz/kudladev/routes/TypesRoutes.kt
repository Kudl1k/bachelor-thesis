package cz.kudladev.routes

import cz.kudladev.data.models.Type
import cz.kudladev.domain.repository.TypesDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.types(typesDAO: TypesDao){
    route("/types"){
        get {
            call.respond(typesDAO.getAllTypes())
        }

        get("{shortcut}"){
            try {
                val shortcut = call.parameters["shortcut"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val type = typesDAO.getTypeByShortcut(shortcut)
                if (type != null){
                    call.respond(type)
                } else {
                    call.respondText(text = "Type with shortcut $shortcut not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of shortcut", status = HttpStatusCode.BadRequest)
            }
        }

        get("{shortcut}/batteries"){
            try {
                val shortcut = call.parameters["shortcut"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val typeWithBatteries = typesDAO.getTypeByShortcutWithBatteries(shortcut)
                if (typeWithBatteries != null){
                    call.respond(typeWithBatteries)
                } else {
                    call.respondText(text = "Type with shortcut $shortcut not found", status = HttpStatusCode.NotFound)
                }

            } catch (e: Exception){
                call.respondText(text = "Please insert right form of shortcut", status = HttpStatusCode.BadRequest)
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

        put("{shortcut}"){
            try {
                val shortcut = call.parameters["shortcut"] ?: return@put call.respond(HttpStatusCode.BadRequest)
                val type = call.receive<Type>()
                val updatedType = typesDAO.updateType(type)
                if (updatedType != null){
                    call.respond(HttpStatusCode.OK, mapOf("id" to type))
                } else {
                    call.respondText(text = "Type with shortcut $shortcut not found", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of shortcut", status = HttpStatusCode.BadRequest)
            }
        }

        delete("{shortcut}"){
            try {
                val shortcut = call.parameters["shortcut"] ?: return@delete call.respond(HttpStatusCode.BadRequest)
                typesDAO.deleteType(shortcut)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of shortcut", status = HttpStatusCode.BadRequest)
            }
        }

    }



}