package cz.kudladev.data.repository

import cz.kudladev.data.*
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerInsert
import cz.kudladev.data.models.ChargerWithTypesAndSizes
import cz.kudladev.data.models.Size
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Timestamp
import java.time.Clock

class ChargersDaoImpl : ChargersDao {
    override suspend fun getAllChargers(): List<ChargerWithTypesAndSizes> {
        return try {
            dbQuery {
                val result = (Chargers leftJoin ChargerTypes leftJoin Types leftJoin ChargerSizes leftJoin Sizes)
                    .selectAll()
                    .toList()
                if (result.isEmpty()) {
                    emptyList()
                } else {
                    result.groupBy { it[Chargers.idCharger] }
                        .map { (chargerId, rows) ->
                            ResultRowParser.resultRowToChargerWithTypes(rows)
                        }
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
                val result = (Chargers leftJoin ChargerTypes leftJoin Types leftJoin ChargerSizes leftJoin Sizes)
                    .selectAll()
                    .where { Chargers.idCharger eq id }
                    .toList()
                if (result.isEmpty()) {
                    null
                } else {
                    val charger = ResultRowParser.resultRowToCharger(result.first())
                    val types = result.mapNotNull { row ->
                        if (row[Types.shortcut] != null) {
                            ResultRowParser.resultRowToType(row)
                        } else {
                            null
                        }
                    }
                    val sizes = result.mapNotNull { row ->
                        if (row[Sizes.name] != null) {
                            Size(name = row[Sizes.name])
                        } else {
                            null
                        }
                    }.toSet()
                    ChargerWithTypesAndSizes(
                        id = charger.id,
                        name = charger.name,
                        tty = charger.tty,
                        baudRate = charger.baudRate,
                        dataBits = charger.dataBits,
                        stopBits = charger.stopBits,
                        parity = charger.parity,
                        rts = charger.rts,
                        dtr = charger.dtr,
                        slots = charger.slots,
                        created_at = charger.created_at!!,
                        types = types,
                        sizes = sizes
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun createCharger(charger: ChargerInsert): ChargerWithTypesAndSizes? {
        return try {
            val timestamp = Clock.systemUTC().instant()
            val insertedId = dbQuery {
                Chargers.insert {
                    it[name] = charger.name
                    it[tty] = charger.tty
                    it[baudRate] = charger.baudRate
                    it[dataBits] = charger.dataBits
                    it[stopBits] = charger.stopBits
                    it[parity] = charger.parity
                    it[rts] = charger.rts
                    it[dtr] = charger.dtr
                    it[slots] = charger.slots
                    it[createdAt] = timestamp
                } get Chargers.idCharger
            }
            for (type in charger.types) {
                if (type != null) {
                    addTypeToCharger(insertedId, type)
                }
            }
            for (size in charger.sizes) {
                if (size != null) {
                    addSizeToCharger(insertedId, size)
                }
            }
            getChargerById(insertedId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateCharger(charger: Charger): Charger? {
        return try {
            val updatedId = dbQuery {
                Chargers.update({ Chargers.idCharger eq charger.id!! }) {
                    it[name] = charger.name
                    it[tty] = charger.tty
                    it[baudRate] = charger.baudRate
                    it[dataBits] = charger.dataBits
                    it[stopBits] = charger.stopBits
                    it[parity] = charger.parity
                    it[rts] = charger.rts
                    it[dtr] = charger.dtr
                    it[slots] = charger.slots
                    it[createdAt] = charger.created_at?.toInstant()!!
                }
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
                Chargers.deleteWhere { Chargers.idCharger eq id } > 0
            }
        } catch (e: Exception) {
            println(e)
            false
        }
    }

    override suspend fun getChargersByType(shortcut: String): List<ChargerWithTypesAndSizes> {
        return try {
            dbQuery {
                val result = (Chargers innerJoin ChargerTypes innerJoin Types innerJoin Sizes)
                    .selectAll()
                    .where { ChargerTypes.typeShortcut eq shortcut }
                    .toList()
                if (result.isEmpty()) {
                    emptyList()
                } else {
                    result.groupBy { it[Chargers.idCharger] }
                        .map { (chargerId, rows) ->
                            ResultRowParser.resultRowToChargerWithTypes(rows)
                        }
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
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


}