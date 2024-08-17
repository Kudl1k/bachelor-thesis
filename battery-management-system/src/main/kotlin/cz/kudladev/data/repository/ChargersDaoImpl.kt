package cz.kudladev.data.repository

import cz.kudladev.data.ChargerTypes
import cz.kudladev.data.Chargers
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Types
import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerWithTypes
import cz.kudladev.domain.repository.ChargersDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.javatime.time
import java.sql.Timestamp
import java.time.Clock

class ChargersDaoImpl : ChargersDao {
    override suspend fun getAllChargers(): List<ChargerWithTypes> {
        return try {
            dbQuery {
                val result = (Chargers leftJoin ChargerTypes leftJoin Types)
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

    override suspend fun getChargerById(id: Int): ChargerWithTypes? {
        return try {
            dbQuery {
                val result = (Chargers leftJoin ChargerTypes leftJoin Types)
                    .selectAll()
                    .where { Chargers.idCharger eq id }
                    .toList()
                if (result.isEmpty()) {
                    null
                } else {
                    val charger = ResultRowParser.resultRowToCharger(result.first())
                    val types = result.mapNotNull { row ->
                        if (row[Types.idType] != null) {
                            ResultRowParser.resultRowToType(row)
                        } else {
                            null
                        }
                    }
                    ChargerWithTypes(
                        id = charger.id,
                        name = charger.name,
                        tty = charger.tty,
                        slots = charger.slots,
                        created_at = charger.created_at!!,
                        types = types
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun createCharger(charger: Charger): Charger? {
        return try {
            val timestamp = Clock.systemUTC().instant()
            val insertedId = dbQuery {
                Chargers.insert {
                    it[name] = charger.name
                    it[tty] = charger.tty
                    it[slots] = charger.slots
                    it[createdAt] = timestamp
                } get Chargers.idCharger
            }
            charger.copy(id = insertedId, created_at = Timestamp.from(timestamp))
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

    override suspend fun getChargersByType(typeId: Int): List<ChargerWithTypes> {
        return try {
            dbQuery {
                val result = (Chargers innerJoin ChargerTypes innerJoin Types)
                    .selectAll()
                    .where { ChargerTypes.idType eq typeId }
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

    override suspend fun addTypeToCharger(chargerId: Int, typeId: Int): ChargerWithTypes? {
        return try {
            val result = dbQuery {
                ChargerTypes.insert {
                    it[idCharger] = chargerId
                    it[idType] = typeId
                }
                getChargerById(chargerId)
            }
            result
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun removeTypeFromCharger(chargerId: Int, typeId: Int): ChargerWithTypes? {
        return try {
            dbQuery {
                ChargerTypes.deleteWhere {
                    (ChargerTypes.idCharger eq chargerId) and (ChargerTypes.idType eq typeId)
                }
                getChargerById(chargerId)
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}