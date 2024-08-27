import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import cz.kudladev.data.entities.*
import org.jetbrains.exposed.dao.toEntity

object DatabaseBuilder {

    private lateinit var hikariDataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:postgresql://localhost:5432/battery"
            username = "admin"
            password = "admin"
            maximumPoolSize = 10
        }
        hikariDataSource = HikariDataSource(config)
        Database.connect(hikariDataSource)

        transaction {
            SchemaUtils.create(Types, Sizes, Batteries, Chargers, ChargerTypes, ChargerSizes, ChargeRecords, ChargeTrackings)
        }

        EntityHook.subscribe { change ->
            if (change.entityClass.isAssignableTo(ChargeTrackingEntity) && change.changeType == EntityChangeType.Created) {
                println("ChargeTracking created: ${change.toEntity(ChargeTrackingEntity)}")
            } else {
                println("Entity change detected: ${change.entityClass} - ${change.changeType}")
            }
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