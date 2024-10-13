package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
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

    override suspend fun getBatteryById(id: String): BatteryFormated? {
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

                BatteryEntity.new(battery.id ?: generateBatteryId()) {
                    typeEntity = type
                    sizeEntity = size
                    cells = battery.cells
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
                    cells = battery.cells
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

    override suspend fun deleteBattery(id: String): BatteryFormated? {
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

    override suspend fun updateBatteryLastChargingCapacity(id: String, capacity: Int): BatteryFormated? {
        return try {
            getBatteryById(id) ?: throw IllegalArgumentException("No battery found for id $id")
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

    override suspend fun getBatteryInfo(id: String): BatteryInfo? {
        return try {
            dbQuery {
                println("id: $id")
                val battery = getBatteryById(id) ?: return@dbQuery null
                println("battery: $battery")

                val chargeRecords = ChargeRecordEntity.find { ChargeRecords.idBattery eq id }.map {
                    val charger = ChargerEntity.findById(it.chargerEntity.id.value) ?: throw IllegalArgumentException("No charger found for id ${it.chargerEntity.id.value}")
                    val tracking = ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq it.id.value }.orderBy(ChargeTrackings.id to SortOrder.ASC).map {
                        EntityParser.toFormatedChargeTracking(it)
                    }
                    val parser = ParserEntity.findById(charger.parser.id.value) ?: throw IllegalArgumentException("No parser found for id ${charger.parser.id.value}")
                    val cells = Cell.selectAll().where { Cell.idChargeRecord eq it.id.value }.orderBy(Cell.number to SortOrder.ASC).map { cell ->
                        val cell = ResultRowParser.resultRowToCell(cell)
                        val cellTracking = CellTracking.selectAll().where { (CellTracking.idChargeRecord eq it.id.value) and (CellTracking.number eq cell.number) }.orderBy(CellTracking.number to SortOrder.ASC).map { tracking ->
                            ResultRowParser.resultRowToFormatedCellTracking(tracking)
                        }
                        CellWithFormatedTracking(
                            idChargeRecord = cell.idChargeRecord,
                            number = cell.number,
                            voltages = cellTracking
                        )
                    }
                    val result = ChargeRecordWithTrackingFormated(
                        idChargeRecord = it.id.value,
                        slot = it.slot,
                        startedAt = Timestamp.from(it.startedAt),
                        finishedAt = it.finishedAt?.let { Timestamp.from(it) },
                        initialCapacity = convertChargedOrDischargedCapacityToMilliAmpHour(it.initialCapacity),
                        chargedCapacity = it.chargedCapacity?.let { it1 ->
                            convertChargedOrDischargedCapacityToMilliAmpHour(
                                it1
                            )
                        },
                        dischargedCapacity = it.dischargedCapacity?.let { it1 ->
                            convertChargedOrDischargedCapacityToMilliAmpHour(
                                it1
                            )
                        },
                        charger = EntityParser.toCharger(charger, EntityParser.toParser(parser)),
                        battery = battery,
                        tracking = tracking,
                        cells = cells
                    )
                    result
                }
                println("chargeRecords: $chargeRecords")
                val result = BatteryInfo(
                    id = battery.id,
                    type = battery.type,
                    size = battery.size,
                    cells = battery.cells,
                    factory_capacity = battery.factory_capacity,
                    voltage = battery.voltage,
                    shop_link = battery.shop_link,
                    last_charged_capacity = battery.last_charged_capacity,
                    archived = battery.archived,
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

    override suspend fun toggleArchiveBattery(id: String): BatteryFormated? {
        return try {
            getBatteryById(id) ?: throw IllegalArgumentException("No battery found for id $id")
            dbQuery {
                BatteryEntity.findById(id)?.apply {
                    archived = !archived
                }
            }
            getBatteryById(id)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override fun generateBatteryId(): String {
        val usedIds = Batteries.slice(Batteries.id).selectAll().map { it[Batteries.id].value.toInt() }.toSet()
        val nextId = (1..Int.MAX_VALUE).first { it.toString() !in usedIds.map { id -> id.toString() } }
        return nextId.toString().padStart(8, '0')
    }
}