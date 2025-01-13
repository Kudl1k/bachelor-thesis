package cz.kudladev.data.repository

import DatabaseBuilder.dbQuery
import cz.kudladev.data.entities.*
import cz.kudladev.data.entities.Cell
import cz.kudladev.data.entities.CellTracking
import cz.kudladev.data.models.*
import cz.kudladev.data.models.ChargeRecord
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.system.stopTracking
import cz.kudladev.util.EntityParser
import cz.kudladev.util.ResultRowParser
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.sql.Timestamp
import java.time.Clock
import java.time.Instant

class ChargeRecordsDaoImpl: ChargeRecordsDao {
    override suspend fun getAllChargeRecords(): List<ChargeRecord> {
        return try {
            dbQuery {
                ChargeRecordEntity.all().map {
                    val charger = ChargerEntity.findById(it.chargerEntity.id.value) ?: throw IllegalArgumentException("No charger found for id ${it.chargerEntity.id.value}")
                    val battery = BatteryEntity.findById(it.batteryEntity.id.value) ?: throw IllegalArgumentException("No battery found for id ${it.batteryEntity.id.value}")
                    val parser = ParserEntity.findById(charger.parser.id.value) ?: throw IllegalArgumentException("No parser found for id ${charger.parser.id.value}")
                    val size = SizeEntity.findById(battery.sizeEntity.id.value) ?: throw IllegalArgumentException("No size found for id ${battery.sizeEntity.id.value}")
                    val type = TypeEntity.findById(battery.typeEntity.id.value) ?: throw IllegalArgumentException("No type found for id ${battery.typeEntity.id.value}")
                    EntityParser.toChargeRecord(it, EntityParser.toCharger(charger, EntityParser.toParser(parser)), EntityParser.toBattery(battery,EntityParser.toType(type),EntityParser.toSize(size)) )
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getChargeRecordById(id: Int): ChargeRecord? {
        return try {
            dbQuery {
                ChargeRecordEntity.findById(id)?.let {
                    val size = SizeEntity.findById(it.batteryEntity.sizeEntity.id.value) ?: throw IllegalArgumentException("No size found for id ${it.batteryEntity.sizeEntity.id.value}")
                    val type = TypeEntity.findById(it.batteryEntity.typeEntity.id.value) ?: throw IllegalArgumentException("No type found for id ${it.batteryEntity.typeEntity.id.value}")
                    val parser = ParserEntity.findById(it.chargerEntity.parser.id.value) ?: throw IllegalArgumentException("No parser found for id ${it.chargerEntity.parser.id.value}")
                    EntityParser.toChargeRecord(it, EntityParser.toCharger(it.chargerEntity, EntityParser.toParser(parser)), EntityParser.toBattery(it.batteryEntity, EntityParser.toType(type),EntityParser.toSize(size)) )
                }
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun createChargeRecord(chargeRecord: ChargeRecordInsert): ChargeRecord {
        val time = Clock.systemUTC().instant()
        return try {
            val insertedId = dbQuery {
                val groupid = getNewChargeGroup()
                val charger = ChargerEntity.find(Chargers.id eq chargeRecord.charger_id).singleOrNull() ?: throw IllegalArgumentException("No charger found for id ${chargeRecord.charger_id}")
                val battery = BatteryEntity.find(Batteries.id eq chargeRecord.battery_id).singleOrNull() ?: throw IllegalArgumentException("No battery found for id ${chargeRecord.battery_id}")
                ChargeRecordEntity.new {
                    groupId = groupid
                    slot = chargeRecord.slot
                    startedAt = time
                    finishedAt = null
                    initialCapacity = 0
                    chargedCapacity = null
                    dischargedCapacity = null
                    chargerEntity = charger
                    batteryEntity = battery
                }.id.value
            }
            getChargeRecordById(insertedId)!!
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateChargeRecord(chargeRecord: ChargeRecord): ChargeRecord {
        return try {
            dbQuery {
                ChargeRecordEntity.findById(chargeRecord.idChargeRecord!!)?.let {
                    it.groupId = chargeRecord.group_id
                    it.checked = chargeRecord.checked
                    it.slot = chargeRecord.slot
                    it.startedAt = chargeRecord.startedAt.toInstant()
                    it.finishedAt = chargeRecord.finishedAt?.toInstant()
                    it.chargedCapacity = chargeRecord.chargedCapacity
                    it.dischargedCapacity = chargeRecord.dischargedCapacity
                    it.chargerEntity = chargeRecord.charger.id?.let { it1 -> ChargerEntity.findById(it1) } ?: throw IllegalArgumentException("No charger found for id ${chargeRecord.charger.id}")
                    it.batteryEntity = chargeRecord.battery.id?.let { it1 -> BatteryEntity.findById(it1) } ?: throw IllegalArgumentException("No battery found for id ${chargeRecord.battery.id}")
                }
            }
            chargeRecord
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteChargeRecord(id: Int): ChargeRecord? {
        return try {
            val chargeRecord = getChargeRecordById(id) ?: throw IllegalArgumentException("No charge record found for id $id")
            dbQuery {
                ChargeRecordEntity.findById(id)?.delete()
            }
            chargeRecord
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun endChargeRecord(id: Int, charged_capacity: Int, discharged_capacity: Int): ChargeRecord? {
        return try {
            dbQuery {
                ChargeRecordEntity.findById(id)?.let {
                    it.finishedAt = Clock.systemUTC().instant()
                    it.chargedCapacity = charged_capacity
                    it.dischargedCapacity = discharged_capacity
                }
            }
            getChargeRecordById(id)
        } catch (e: Exception) {
            println(e)
            null
        }
    }

    override suspend fun getNotEndedChargeRecordsWithTracking(): List<ChargeRecordWithTracking> {
        return try {
            dbQuery {
                // Get the latest group ID
                val latestGroupId = ChargeRecordEntity.all().map { it.groupId }.max() ?: 0

                // Check if all records in the latest group have ended
                val allEnded = ChargeRecordEntity.find { ChargeRecords.groupId eq latestGroupId }
                    .all { it.finishedAt != null && it.checked }

                if (allEnded) {
                    return@dbQuery emptyList<ChargeRecordWithTracking>()
                }

                // Find charge records with the latest group ID
                ChargeRecordEntity.find { ChargeRecords.groupId eq latestGroupId }
                    .orderBy(ChargeRecords.slot to SortOrder.ASC)
                    .map { it ->
                    val charger = ChargerEntity.findById(it.chargerEntity.id.value) ?: throw IllegalArgumentException("No charger found for id ${it.chargerEntity.id.value}")
                    val battery = BatteryEntity.findById(it.batteryEntity.id.value) ?: throw IllegalArgumentException("No battery found for id ${it.batteryEntity.id.value}")
                    val size = SizeEntity.findById(battery.sizeEntity.id.value) ?: throw IllegalArgumentException("No size found for id ${battery.sizeEntity.id.value}")
                    val type = TypeEntity.findById(battery.typeEntity.id.value) ?: throw IllegalArgumentException("No type found for id ${battery.typeEntity.id.value}")
                    val tracking = ChargeTrackingEntity.find { ChargeTrackings.idChargeRecord eq it.id.value }.orderBy(ChargeTrackings.id to SortOrder.ASC).map {
                        EntityParser.toFormatedChargeTracking(it)
                    }
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
                    val parser = ParserEntity.findById(charger.parser.id.value) ?: throw IllegalArgumentException("No parser found for id ${charger.parser.id.value}")
                    ChargeRecordWithTracking(
                        idChargeRecord = it.id.value,
                        group_id = it.groupId,
                        checked = it.checked,
                        slot = it.slot,
                        startedAt = Timestamp.from(it.startedAt),
                        finishedAt = it.finishedAt?.let { Timestamp.from(it) },
                        initialCapacity = it.initialCapacity,
                        chargedCapacity = it.chargedCapacity,
                        dischargedCapacity = it.dischargedCapacity,
                        charger = EntityParser.toCharger(charger, EntityParser.toParser(parser)),
                        battery = EntityParser.toFormatedBattery(battery, EntityParser.toType(type), EntityParser.toSize(size)),
                        tracking = tracking,
                        cells = cells
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun checkChargeRecords(): List<ChargeRecord> {
        return try {
            dbQuery {
                val latestGroupId = ChargeRecordEntity.all().map { it.groupId }.max() ?: 0
                if (latestGroupId == 0) {
                    return@dbQuery emptyList<ChargeRecord>()
                }
                ChargeRecordEntity.find { ChargeRecords.groupId eq latestGroupId }.forEach {
                    it.checked = true
                    if (it.finishedAt == null) {
                        it.finishedAt = Instant.now()
                    }
                }

                ChargeRecordEntity.find { ChargeRecords.groupId eq latestGroupId }.map {
                    EntityParser.toChargeRecord(
                        it,
                        EntityParser.toCharger(it.chargerEntity, EntityParser.toParser(it.chargerEntity.parser)),
                        EntityParser.toBattery(it.batteryEntity, EntityParser.toType(it.batteryEntity.typeEntity), EntityParser.toSize(it.batteryEntity.sizeEntity))
                    )
                }
            }
        } catch (e: Exception) {
            println(e)
            emptyList()
        }
    }

    override suspend fun getNewChargeGroup(): Int {
        return try {
            dbQuery {
                val latestGroupId = ChargeRecordEntity.all().map { it.groupId }.max() ?: 0
                if (latestGroupId == 0) {
                    return@dbQuery 1
                }
                val allEnded = ChargeRecordEntity.find { ChargeRecords.groupId eq latestGroupId }
                    .all { it.finishedAt != null && it.checked }
                if (allEnded) {
                    return@dbQuery latestGroupId + 1
                } else {
                    return@dbQuery latestGroupId
                }
            }
        } catch (e: Exception) {
            println(e)
            1
        }
    }

    override suspend fun endGroupChargeRecords(group: Int): Boolean {
        return try {
            dbQuery {
                ChargeRecordEntity.find { ChargeRecords.groupId eq group }.forEach {
                    if (it.finishedAt == null) {
                        it.finishedAt = Instant.now()
                    }
                    val lastChargingChargeTracking = ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq it.id.value) and (ChargeTrackings.charging eq true) }.orderBy(ChargeTrackings.id to SortOrder.DESC).firstOrNull()
                    val lastDischargingChargeTracking = ChargeTrackingEntity.find { (ChargeTrackings.idChargeRecord eq it.id.value) and (ChargeTrackings.charging eq false) }.orderBy(ChargeTrackings.id to SortOrder.DESC).firstOrNull()
                    it.chargedCapacity = lastChargingChargeTracking?.capacity
                    it.dischargedCapacity = lastDischargingChargeTracking?.capacity
                    it.checked = true
                }
            }
            true
        } catch (e: Exception) {
            println(e)
            false
        }
    }
}