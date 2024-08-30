package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.Size
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.util.EntityParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Timestamp
import java.time.Clock

class ChargersDaoImpl : ChargersDao {
    override suspend fun getAllChargers(): List<ChargerWithTypesAndSizes> {
        return try {
            dbQuery {
                val chargers = Chargers.selectAll().map { ChargerEntity.wrapRow(it) }

                // Fetch related Types and Sizes for each Charger
                chargers.map { chargerEntity ->
                    // Fetch related types
                    val types = (ChargerTypes innerJoin Types)
                        .select { ChargerTypes.idCharger eq chargerEntity.id.value }
                        .map { TypeEntity.wrapRow(it) }
                        .map { Type(it.id.value, it.name) }
                    println(types)

                    // Fetch related sizes
                    val sizes = (ChargerSizes innerJoin Sizes)
                        .select { ChargerSizes.idCharger eq chargerEntity.id.value }
                        .map { SizeEntity.wrapRow(it) }
                        .map { Size(it.id.value) }
                        .toSet()
                    println(sizes)

                    // Map to ChargerWithTypesAndSizes
                    EntityParser.toChargerWithTypesAndSizes(chargerEntity, types, sizes)
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargerById(id: Int): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                val chargerRow = Chargers.select { Chargers.id eq id }.singleOrNull() ?: return@dbQuery null
                val chargerEntity = ChargerEntity.wrapRow(chargerRow)
                println(chargerEntity)

                // Fetch related types
                println(ChargerTypes leftJoin Types)
                val types = (ChargerTypes leftJoin Types)
                    .select { ChargerTypes.idCharger eq id }
                    .map { TypeEntity.wrapRow(it) }
                    .map { Type(it.id.value, it.name) }

                println(types)

                // Fetch related sizes
                val sizes = (ChargerSizes leftJoin Sizes)
                    .select { ChargerSizes.idCharger eq id }
                    .map { SizeEntity.wrapRow(it) }
                    .map { Size(it.name.value) }
                    .toSet()

                println(sizes)

                EntityParser.toChargerWithTypesAndSizes(chargerEntity, types, sizes)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getChargerByTypesAndSizes(
        searchCharger: SearchCharger
    ): List<ChargerWithTypesAndSizes> {
        return try {
            dbQuery {
                // Start the query on the Chargers table
                val query = Chargers
                    .leftJoin(ChargerTypes)
                    .leftJoin(ChargerSizes)
                    .selectAll()
                    .apply {
                        // If types are provided, filter by them
                        if (searchCharger.types.isNotEmpty()) {
                            andWhere { ChargerTypes.typeShortcut inList searchCharger.types }
                        }
                        // If sizes are provided, filter by them
                        if (searchCharger.sizes.isNotEmpty()) {
                            andWhere { ChargerSizes.sizeName inList searchCharger.sizes }
                        }
                    }

                // Execute the query and map the results to ChargerWithTypesAndSizes
                query.map { row ->
                    val chargerId = row[Chargers.id].value
                    val chargerName = row[Chargers.name]
                    val chargerTty = row[Chargers.tty]
                    val chargerBaudRate = row[Chargers.baudRate]
                    val chargerDataBits = row[Chargers.dataBits]
                    val chargerStopBits = row[Chargers.stopBits]
                    val chargerParity = row[Chargers.parity]
                    val chargerRts = row[Chargers.rts]
                    val chargerDtr = row[Chargers.dtr]
                    val chargerSlots = row[Chargers.slots]
                    val chargerCreatedAt = row[Chargers.createdAt]

                    // Fetch associated types and sizes for the charger
                    val types = ChargerTypes
                        .select { ChargerTypes.idCharger eq chargerId }
                        .map { TypeEntity.findById(it[ChargerTypes.typeShortcut]) }

                    val sizes = ChargerSizes
                        .select { ChargerSizes.idCharger eq chargerId }
                        .map { SizeEntity.findById(it[ChargerSizes.sizeName]) }

                    // Map to ChargerWithTypesAndSizes
                    ChargerWithTypesAndSizes(
                        id = chargerId,
                        name = chargerName,
                        tty = chargerTty,
                        baudRate = chargerBaudRate,
                        dataBits = chargerDataBits,
                        stopBits = chargerStopBits,
                        parity = chargerParity,
                        rts = chargerRts,
                        dtr = chargerDtr,
                        slots = chargerSlots,
                        created_at = Timestamp.from(chargerCreatedAt),
                        types = types.filterNotNull().map { EntityParser.toType(it) }, // Handle nulls
                        sizes = sizes.filterNotNull().map { EntityParser.toSize(it) }.toSet() // Handle nulls
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun createCharger(charger: ChargerInsert): ChargerWithTypesAndSizes? {
        return try {
            val insertedId = dbQuery {
                ChargerEntity.new {
                    name = charger.name
                    tty = charger.tty
                    baudRate = charger.baudRate
                    dataBits = charger.dataBits
                    stopBits = charger.stopBits
                    parity = charger.parity
                    rts = charger.rts
                    dtr = charger.dtr
                    slots = charger.slots
                    createdAt = Clock.systemUTC().instant()
                }.id
            }
            for (type in charger.types) {
                if (type != null) {
                    println(insertedId.value)
                    println(type)
                    addTypeToCharger(insertedId.value, type)
                }
            }
            for (size in charger.sizes) {
                if (size != null) {
                    addSizeToCharger(insertedId.value, size)
                }
            }
            getChargerById(insertedId.value)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateCharger(charger: Charger): Charger? {
        return try {
            val updatedId = dbQuery {
                ChargerEntity.findById(charger.id ?: throw IllegalArgumentException("Charger id must not be null"))?.apply {
                    name = charger.name
                    tty = charger.tty
                    baudRate = charger.baudRate
                    dataBits = charger.dataBits
                    stopBits = charger.stopBits
                    parity = charger.parity
                    rts = charger.rts
                    dtr = charger.dtr
                    slots = charger.slots
                    createdAt = charger.created_at?.toInstant() ?: Clock.systemUTC().instant()
                }?.id?.value ?: throw IllegalArgumentException("No charger found for id ${charger.id}")
            }
            charger.copy(id = updatedId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun deleteCharger(id: Int): Boolean {
        return try {
            dbQuery {
                ChargerEntity.findById(id)?.delete() ?: false
                true
            }
        } catch (e: Exception) {
            println(e)
            false
        }
    }


    override suspend fun addTypeToCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                ChargerTypes.insert {
                    it[idCharger] = chargerId
                    it[typeShortcut] = shortcut
                }
            }
            getChargerById(chargerId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun removeTypeFromCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                ChargerTypes.deleteWhere {
                    (ChargerTypes.idCharger eq chargerId) and (ChargerTypes.typeShortcut eq shortcut)
                }
            }
            getChargerById(chargerId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun addSizeToCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                ChargerSizes.insert {
                    it[idCharger] = chargerId
                    it[sizeName] = size
                }
            }
            getChargerById(chargerId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun removeSizeFromCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                ChargerSizes.deleteWhere {
                    (ChargerSizes.idCharger eq chargerId) and (ChargerSizes.sizeName eq size)
                }
            }
            getChargerById(chargerId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updatePort(chargerId: Int, port: String): ChargerWithTypesAndSizes? {
        return try {
            dbQuery {
                Chargers.update({ Chargers.id eq chargerId }) {
                    it[tty] = port
                }
            }
            getChargerById(chargerId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }


}