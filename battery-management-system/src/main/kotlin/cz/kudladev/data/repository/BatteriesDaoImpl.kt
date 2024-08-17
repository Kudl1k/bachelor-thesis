package cz.kudladev.data.repository

import cz.kudladev.data.Batteries
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Types
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.Type
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Time
import java.sql.Timestamp
import java.time.Clock

class BatteriesDaoImpl: BatteriesDao {



    override suspend fun getAllBatteries(): List<Battery> {
        return try {
            dbQuery{
                (Batteries innerJoin Types).selectAll().map { ResultRowParser.resultRowToBattery(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBatteryById(id: Int): Battery? {
        return try {
            dbQuery {
                (Batteries innerJoin Types).selectAll().where { Batteries.idBattery eq id }.map { ResultRowParser.resultRowToBattery(it) }.singleOrNull()
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun createBattery(battery: Battery): Battery? {
        return try {
            if (battery.type.id == null) {
                throw IllegalArgumentException("Type id is null")
            }
            val insertedId =  dbQuery {
                Types.select({ Types.idType eq battery.type.id }).singleOrNull() ?: throw IllegalArgumentException("No type found for id ${battery.type.id}")
                Batteries.insert {
                    it[size] = battery.size
                    it[idType] = battery.type.id
                    it[factoryCapacity] = battery.factory_capacity
                    it[voltage] = battery.voltage
                    it[lastChargedCapacity] = battery.last_charged_capacity
                    it[lastTimeChargedAt] = battery.last_time_charged_at?.toInstant()
                    it[createdAt] = Clock.systemUTC().instant()
                } get Batteries.idBattery
            }
            battery.copy(id = insertedId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateBattery(battery: Battery): Battery? {
        return try {
            val insertedId = dbQuery {
                Batteries.update({ Batteries.idBattery eq battery.id!! }) {
                    it[size] = battery.size
                    it[factoryCapacity] = battery.factory_capacity
                    it[voltage] = battery.voltage
                    it[lastChargedCapacity] = battery.last_charged_capacity
                    it[lastTimeChargedAt] = battery.last_time_charged_at?.toInstant()
                    it[createdAt] = Clock.systemUTC().instant()
                }
            }
            battery.copy(id = insertedId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun deleteBattery(id: Int): Battery? {
        return try {
            val battery = getBatteryById(id) ?: throw IllegalArgumentException("No battery found for id $id")
            dbQuery {
                Batteries.deleteWhere { Batteries.idBattery eq id }
            }
            battery
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}