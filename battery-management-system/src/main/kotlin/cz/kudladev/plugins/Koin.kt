package cz.kudladev.plugins

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.ChargersDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.domain.repository.TypesDao
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
}

