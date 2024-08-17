package cz.kudladev.plugins

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.domain.repository.TypesDao
import cz.kudladev.routes.batteries
import cz.kudladev.routes.chargers
import cz.kudladev.routes.types
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val typesDao by inject<TypesDao>()
    val batteriesDao by inject<BatteriesDao>()
    val chargersDao by inject<ChargersDao>()


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        types(typesDao)
        batteries(batteriesDao)
        chargers(chargersDao)
    }
}
