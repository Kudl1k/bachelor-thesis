import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import cz.kudladev.data.entities.*
import cz.kudladev.util.EntityParser
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.broadcast
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.toEntity

object DatabaseBuilder {

    private lateinit var hikariDataSource: HikariDataSource

    @OptIn(ObsoleteCoroutinesApi::class)
    val broadcastChannel = BroadcastChannel<String>(Channel.BUFFERED)

    @OptIn(ObsoleteCoroutinesApi::class)
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
            if (change.entityClass.isAssignableTo(ChargeTrackingEntity)){

                if (change.changeType == EntityChangeType.Created){
                    val chargeTracking = change.toEntity(ChargeTrackingEntity) ?: return@subscribe
                    runBlocking {
                        broadcastChannel.send(Json.encodeToString(EntityParser.toFormatedChargeTracking(chargeTracking)))
                    }
                }
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