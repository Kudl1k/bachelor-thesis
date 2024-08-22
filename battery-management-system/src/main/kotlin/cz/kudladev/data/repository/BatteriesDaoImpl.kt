package cz.kudladev.data.repository

import cz.kudladev.data.Batteries
import cz.kudladev.data.DatabaseBuilder.dbQuery
import cz.kudladev.data.Sizes
import cz.kudladev.data.Types
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.BatteryInsert
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Clock

class BatteriesDaoImpl: BatteriesDao {



    override suspend fun getAllBatteries(): List<Battery> {
        return try {
            dbQuery{
                (Batteries innerJoin Types innerJoin Sizes).selectAll().map { ResultRowParser.resultRowToBattery(it) }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getBatteryById(id: Int): Battery? {
        return try {
            dbQuery {
                (Batteries innerJoin Types innerJoin Sizes).selectAll().where { Batteries.idBattery eq id }.map { ResultRowParser.resultRowToBattery(it) }.singleOrNull()
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun createBattery(battery: BatteryInsert): Battery? {
        return try {
            val type = dbQuery {
                Types.select { Types.name eq battery.type }.singleOrNull() ?: throw IllegalArgumentException("No type found for name ${battery.type}")
            }
            val sizeres = dbQuery {
                Sizes.select { Sizes.name eq battery.size }.singleOrNull() ?: throw IllegalArgumentException("No size found for name ${battery.size}")
            }
            val insertedId = dbQuery {
                Batteries.insert {
                    it[factoryCapacity] = battery.factory_capacity
                    it[voltage] = battery.voltage
                    it[lastChargedCapacity] = null
                    it[lastTimeChargedAt] = null
                    it[createdAt] = Clock.systemUTC().instant()
                    it[typeShortcut] = type[Types.shortcut]
                    it[size] = sizeres[Sizes.name]
                } get Batteries.idBattery
            }
            val createdBattery = getBatteryById(insertedId) ?: throw IllegalArgumentException("No battery found for id $insertedId")
            createdBattery
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun updateBattery(battery: Battery): Battery? {
        return try {
            val insertedId = dbQuery {
                Batteries.update({ Batteries.idBattery eq battery.id!! }) {
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