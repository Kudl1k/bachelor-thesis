package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.util.EntityParser
import org.jetbrains.exposed.sql.*
import java.sql.Timestamp
import java.time.Clock

class BatteriesDaoImpl: BatteriesDao {

    override suspend fun getAllBatteries(): List<BatteryFormated> {
        return try {
            dbQuery {
                BatteryEntity.all().orderBy(Batteries.id to SortOrder.ASC).map {
                    val size = SizeEntity.findById(it.sizeEntity.id.value) ?: throw IllegalArgumentException("No size found for id ${it.sizeEntity.id.value}")
                    val type = TypeEntity.findById(it.typeEntity.id.value) ?: throw IllegalArgumentException("No type found for id ${it.typeEntity.id.value}")

                    // When converting to your model, use the .value property
                    EntityParser.toFormatedBattery(
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

    override suspend fun getBatteryById(id: Int): BatteryFormated? {
        return try {
            dbQuery {
                val batteryEntity = BatteryEntity.findById(id) ?: return@dbQuery null

                // Fetch associated TypeEntity and SizeEntity
                val typeEntity = batteryEntity.typeEntity
                val sizeEntity = batteryEntity.sizeEntity

                EntityParser.toFormatedBattery(
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

    override suspend fun createBattery(battery: BatteryInsert): BatteryFormated? {
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
                    shopLink = battery.shop_link
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

    override suspend fun updateBattery(battery: Battery): BatteryFormated? {
        return try {
            val insertedId = dbQuery {
                BatteryEntity.findById(battery.id ?: throw IllegalArgumentException("Battery id must not be null"))?.apply {
                    typeEntity = TypeEntity.findById(battery.type.shortcut) ?: throw IllegalArgumentException("No type found for id ${battery.type.shortcut}")
                    sizeEntity = SizeEntity.findById(battery.size.name) ?: throw IllegalArgumentException("No size found for id ${battery.size.name}")
                    factoryCapacity = battery.factory_capacity
                    voltage = battery.voltage
                    shopLink = battery.shop_link
                    lastChargedCapacity = battery.last_charged_capacity
                    lastTimeChargedAt = battery.last_time_charged_at?.toInstant()
                    createdAt = battery.created_at?.toInstant() ?: Clock.systemUTC().instant()
                }?.id?.value ?: throw IllegalArgumentException("No battery found for id ${battery.id}")
            }
            getBatteryById(insertedId)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun deleteBattery(id: Int): BatteryFormated? {
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

    override suspend fun updateBatteryLastChargingCapacity(id: Int, capacity: Int): BatteryFormated? {
        return try {
            val battery = getBatteryById(id) ?: throw IllegalArgumentException("No battery found for id $id")
            dbQuery {
                BatteryEntity.findById(id)?.apply {
                    lastChargedCapacity = capacity
                    lastTimeChargedAt = Clock.systemUTC().instant()
                }
            }
            getBatteryById(id)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getBatteryInfo(id: Int): BatteryInfo? {
        return try {
            dbQuery {
                val battery = getBatteryById(id) ?: return@dbQuery null


                val chargeRecords = ChargeRecordEntity.find { ChargeRecords.idBattery eq id }.map {
                    val charger = ChargerEntity.findById(it.chargerEntity.id.value) ?: throw IllegalArgumentException("No charger found for id ${it.chargerEntity.id.value}")
                    val tracking = ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq it.id.value }.orderBy(ChargeTrackings.id to SortOrder.ASC).map { EntityParser.toFormatedChargeTracking(it) }
                    val result = ChargeRecordWithTrackingFormated(
                        idChargeRecord = it.id.value,
                        program = it.program,
                        slot = it.slot,
                        startedAt = Timestamp.from(it.startedAt),
                        finishedAt = it.finishedAt?.let { Timestamp.from(it) },
                        chargedCapacity = it.chargedCapacity?.let { it1 ->
                            convertChargedOrDischargedCapacityToMilliAmpHour(
                                it1
                            )
                        },
                        charger = EntityParser.toCharger(charger),
                        battery = battery,
                        tracking = tracking
                    )
                    result
                }
                val result = BatteryInfo(
                    id = battery.id!!,
                    type = battery.type,
                    size = battery.size,
                    factory_capacity = battery.factory_capacity,
                    voltage = battery.voltage,
                    shop_link = battery.shop_link,
                    last_charged_capacity = battery.last_charged_capacity,
                    last_time_charged_at = battery.last_time_charged_at,
                    created_at = battery.created_at,
                    charge_records = chargeRecords
                )
                result
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}