package cz.kudladev.plugins

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
import cz.kudladev.domain.repository.*
import cz.kudladev.routes.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
import org.koin.ktor.ext.inject

fun Application.configureRouting() {
    val typesDao by inject<TypesDao>()
    val sizesDao by inject<SizeDao>()
    val batteriesDao by inject<BatteriesDao>()
    val chargersDao by inject<ChargersDao>()
    val chargerRecordsDao by inject<ChargeRecordsDao>()
    val chargerTrackingDao by inject<ChargeTrackingDao>()
    val cellDao by inject<CellDao>()


    routing {
        get("/") {
            call.respondText("Hello World!")
        }
        types(typesDao)
        sizes(sizesDao)
        batteries(batteriesDao)
        chargers(chargersDao)
        chargerrecords(chargerRecordsDao)
        chargetrackings(
            chargerTrackingDao,
            chargerRecordsDao,
            chargersDao,
            batteriesDao,
            cellDao
        )
    }
}
