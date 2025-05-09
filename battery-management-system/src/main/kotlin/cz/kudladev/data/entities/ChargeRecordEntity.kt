package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object ChargeRecords : IntIdTable("charge_record") {
    val groupId = integer("group_id")
    val checked = bool("checked").default(false)
    val slot = integer("slot")
    val startedAt = timestamp("started_at")
    val finishedAt = timestamp("finished_at").nullable()
    val initialCapacity = integer("initial_capacity")
    val chargedCapacity = integer("charged_capacity").nullable()
    val dischargedCapacity = integer("discharged_capacity").nullable()
    val idCharger = reference("id_charger", Chargers)
    val idBattery = reference("id_battery", Batteries.id)
}

class ChargeRecordEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChargeRecordEntity>(ChargeRecords)

    var groupId by ChargeRecords.groupId
    var checked by ChargeRecords.checked
    var slot by ChargeRecords.slot
    var startedAt by ChargeRecords.startedAt
    var finishedAt by ChargeRecords.finishedAt
    var chargedCapacity by ChargeRecords.chargedCapacity
    var dischargedCapacity by ChargeRecords.dischargedCapacity
    var initialCapacity by ChargeRecords.initialCapacity
    var chargerEntity by ChargerEntity referencedOn ChargeRecords.idCharger
    var batteryEntity by BatteryEntity referencedOn ChargeRecords.idBattery
}