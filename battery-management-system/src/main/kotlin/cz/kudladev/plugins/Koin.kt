package cz.kudladev.plugins

import cz.kudladev.data.repository.BatteriesDaoImpl
import cz.kudladev.data.repository.TypesDaoImpl
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

    single<TypesDaoImpl> { TypesDaoImpl() }
    single<BatteriesDaoImpl> { BatteriesDaoImpl() }

}

