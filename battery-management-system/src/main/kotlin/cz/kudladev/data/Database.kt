import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import cz.kudladev.data.entities.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel

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
            SchemaUtils.createMissingTablesAndColumns(Types, Sizes, Batteries, Parser, Chargers, ChargerTypes, ChargerSizes, ChargeRecords, ChargeTrackings,Cell, CellTracking)
        }
        transaction {
            val existingParsers = ParserEntity.all().toList()
            if (existingParsers.isEmpty()) {
                ParserEntity.new {
                    name = "Conrad Charge Manager 2010"
                }
                ParserEntity.new {
                    name = "Turnigy Accucel 6"
                }
                println("Inserted default parsers")
            } else {
                println("Parsers already exist")
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