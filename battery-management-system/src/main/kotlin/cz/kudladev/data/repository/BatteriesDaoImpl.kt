package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.*
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.BatteryInsert
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.util.EntityParser
import org.jetbrains.exposed.sql.*
import java.time.Clock

class BatteriesDaoImpl: BatteriesDao {

    override suspend fun getAllBatteries(): List<Battery> {
        return try {
            dbQuery {
                BatteryEntity.all().map {
                    val size = SizeEntity.findById(it.sizeEntity.id.value) ?: throw IllegalArgumentException("No size found for id ${it.sizeEntity.id.value}")
                    val type = TypeEntity.findById(it.typeEntity.id.value) ?: throw IllegalArgumentException("No type found for id ${it.typeEntity.id.value}")

                    // When converting to your model, use the .value property
                    EntityParser.toBattery(
                        it,
                        EntityParser.toType(type),
                        EntityParser.toSize(size)
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getBatteryById(id: Int): Battery? {
        return try {
            dbQuery {
                val batteryEntity = BatteryEntity.findById(id) ?: return@dbQuery null

                // Fetch associated TypeEntity and SizeEntity
                val typeEntity = batteryEntity.typeEntity
                val sizeEntity = batteryEntity.sizeEntity

                EntityParser.toBattery(
                    batteryEntity,
                    EntityParser.toType(typeEntity),
                    EntityParser.toSize(sizeEntity)
                )
            }
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun createBattery(battery: BatteryInsert): Battery? {
        return try {
            val insertedId = dbQuery {
                val type = TypeEntity.find { Types.shortcut eq battery.type }.singleOrNull()
                    ?: throw IllegalArgumentException("No type found for shortcut ${battery.type}")
                val size = SizeEntity.find { Sizes.name eq battery.size }.singleOrNull()
                    ?: throw IllegalArgumentException("No size found for name ${battery.size}")

                BatteryEntity.new {
                    typeEntity = type
                    sizeEntity = size
                    factoryCapacity = battery.factory_capacity
                    voltage = battery.voltage
                    lastChargedCapacity = null
                    lastTimeChargedAt = null
                    createdAt = Clock.systemUTC().instant()
                }.id.value
            }
            getBatteryById(insertedId)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateBattery(battery: Battery): Battery? {
        return try {
            val insertedId = dbQuery {
                BatteryEntity.findById(battery.id ?: throw IllegalArgumentException("Battery id must not be null"))?.apply {
                    typeEntity = TypeEntity.findById(battery.type.shortcut) ?: throw IllegalArgumentException("No type found for id ${battery.type.shortcut}")
                    sizeEntity = SizeEntity.findById(battery.size.name) ?: throw IllegalArgumentException("No size found for id ${battery.size.name}")
                    factoryCapacity = battery.factory_capacity
                    voltage = battery.voltage
                    lastChargedCapacity = battery.last_charged_capacity
                    lastTimeChargedAt = battery.last_time_charged_at?.toInstant()
                    createdAt = battery.created_at?.toInstant() ?: Clock.systemUTC().instant()
                }?.id?.value ?: throw IllegalArgumentException("No battery found for id ${battery.id}")
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
                BatteryEntity.findById(id)?.delete()
            }
            battery
        } catch (e: Exception) {
            println(e)
            null
        }
    }
}