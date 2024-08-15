package cz.kudladev.backend.plugins

import cz.kudladev.backend.service.BatteryService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.config.yaml.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.sql.*
import kotlinx.coroutines.*

fun Application.configureDatabases() {
    val dbConnection: Connection = connectToPostgres(embedded = true)

    val batteryService = BatteryService(dbConnection)

    routing {

    }
}

fun connectToPostgres(embedded: Boolean): Connection {
    Class.forName("org.postgresql.Driver")
    if (embedded) {
        return DriverManager.getConnection("jdbc:postgresql://localhost/battery", "admin", "admin")
    } else {
        val configs = YamlConfig("postgres.yaml")
        val url = "jdbc:postgresql://localhost:5432/" +
                configs?.property("services.postgres.environment.POSTGRES_DB")?.getString()
        val user = configs?.property("services.postgres.environment.POSTGRES_USER")?.getString()
        val password = configs?.property("services.postgres.environment.POSTGRES_PASSWORD")?.getString()
        return DriverManager.getConnection(url, user, password)
    }
}