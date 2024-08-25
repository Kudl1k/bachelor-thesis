package cz.kudladev.data

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseBuilder {

    fun init() {
        val driverClassName = "org.postgresql.Driver"
        val jdbcURL = "jdbc:postgresql://localhost:5432/battery"
        val user = "admin"
        val password = "admin"
        val database = Database.connect(jdbcURL, driverClassName, user, password)

        transaction {
            SchemaUtils.create(Types, Batteries, Chargers, ChargerTypes, ChargerSizes, ChargeRecords, ChargeTracking)
        }


    }

    /**
     * Exposed transactions are blocking. Here I
     * start each query in its own coroutine and make
     * DB calls async on the [Dispatchers.IO] thread.
     */
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }
}