package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ChargeRecords : IntIdTable("charge_record") {
    val program = varchar("program", 1) // Assuming CHAR is stored as a single-character VARCHAR
    val slot = integer("slot")
    val startedAt = timestamp("started_at")
    val finishedAt = timestamp("finished_at").nullable()
    val chargedCapacity = integer("charged_capacity").nullable()
    val idCharger = reference("id_charger", Chargers)
    val idBattery = reference("id_battery", Batteries)
}

class ChargeRecordEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChargeRecordEntity>(ChargeRecords)

    var program by ChargeRecords.program
    var slot by ChargeRecords.slot
    var startedAt by ChargeRecords.startedAt
    var finishedAt by ChargeRecords.finishedAt
    var chargedCapacity by ChargeRecords.chargedCapacity
    var chargerEntity by ChargerEntity referencedOn ChargeRecords.idCharger
    var batteryEntity by BatteryEntity referencedOn ChargeRecords.idBattery
}