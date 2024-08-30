package cz.kudladev.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*


fun Application.configureCORS() {
    install(CORS) {
        anyHost()
        allowHeader(HttpHeaders.ContentType)
        allowHeader(HttpHeaders.Authorization)
        methods.add(HttpMethod.Get)
        methods.add(HttpMethod.Post)
        methods.add(HttpMethod.Put)
        methods.add(HttpMethod.Delete)

    }
}