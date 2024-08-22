package cz.kudladev.routes

import cz.kudladev.data.models.Size
import cz.kudladev.domain.repository.SizeDao
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.sizes(sizesDao: SizeDao){
    route("/sizes"){
        get {
            call.respond(sizesDao.getAllSizes())
        }
        get("{name}") {
            try {
                val name = call.parameters["name"] ?: return@get call.respondText("Missing or malformed name", status = HttpStatusCode.BadRequest)
                val size = sizesDao.getSizeByName(name)
                if (size != null) {
                    call.respond(size)
                } else {
                    call.respondText("No size with name $name", status = HttpStatusCode.NotFound)
                }
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of name", status = HttpStatusCode.BadRequest)
            }
        }
        post {
            try {
                val size = call.receive<Size>()
                val id = sizesDao.insertSize(size.name)
                call.respond(HttpStatusCode.Created, mapOf("id" to id))
            } catch (e: Exception){
                call.respondText(text = "Please fill all fields", status = HttpStatusCode.BadRequest)
            }
        }
        delete("{name}") {
            try {
                val name = call.parameters["name"] ?: return@delete call.respondText("Missing or malformed name", status = HttpStatusCode.BadRequest)
                sizesDao.deleteSize(name)
                call.respond(HttpStatusCode.OK)
            } catch (e: Exception){
                call.respondText(text = "Please insert right form of name", status = HttpStatusCode.BadRequest)
            }
        }
    }
}