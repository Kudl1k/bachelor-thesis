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
            call.respond(typesDAO.getTypeById(call.parameters["id"]?.toInt() ?: 0) ?: HttpStatusCode.NotFound)
        }

        get("{id}/batteries"){
            call.respond(typesDAO.getTypeByIdWithBatteries(call.parameters["id"]?.toInt() ?: 0) ?: HttpStatusCode.NotFound)
        }

        post {
            val type = call.receive<Type>()
            val id = typesDAO.insertType(type)
            call.respond(HttpStatusCode.Created, mapOf("id" to id))
        }

        put("{id}"){
            val type = call.receive<Type>()
            val id = typesDAO.updateType(type)
            call.respond(HttpStatusCode.OK, mapOf("id" to id))
        }

        delete("{id}"){
            val id = call.parameters["id"]?.toInt() ?: 0
            typesDAO.deleteType(id)
            call.respond(HttpStatusCode.OK)
        }

    }



}