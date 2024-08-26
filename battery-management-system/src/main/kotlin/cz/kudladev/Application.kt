package cz.kudladev

import cz.kudladev.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.websocket.*
import java.time.Duration

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(WebSockets) {
        pingPeriod = Duration.ofMinutes(1)
        timeout = Duration.ofSeconds(15)
    }
    configureSerialization()
    configureKoin()
    DatabaseBuilder.init()
    configureRouting()
    configureCORS()
}
