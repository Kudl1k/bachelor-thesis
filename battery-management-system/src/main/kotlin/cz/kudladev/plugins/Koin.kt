package cz.kudladev.plugins

import cz.kudladev.data.repository.*
import cz.kudladev.domain.repository.*
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()
        modules(
            batteryModule
        )
    }
}

val batteryModule = module {

    single<TypesDao> { TypesDaoImpl() }
    single<BatteriesDao> { BatteriesDaoImpl() }
    single<ChargersDao> { ChargersDaoImpl() }
    single<ChargeRecordsDao> { ChargeRecordsDaoImpl() }
    single<ChargeTrackingDao> { ChargeTrackingDaoImpl() }
    single<SizeDao> { SizeDaoImpl() }

}

