package cz.kudladev.plugins

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
import cz.kudladev.routes.batteries
import cz.kudladev.routes.types
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val typesDao by inject<TypesDaoImpl>()
    val batteriesDao by inject<BatteriesDaoImpl>()


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        types(typesDao)
        batteries(batteriesDao)
    }
}
