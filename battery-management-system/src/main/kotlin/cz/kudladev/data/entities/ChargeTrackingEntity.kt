package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant


// Charge_Tracking Table
object ChargeTrackings : IdTable<Instant>("charge_tracking") {
    override val id = timestamp("timestamp").entityId()
    val idChargeRecord = reference("id_charge_record", ChargeRecords)
    val capacity = integer("capacity")
    val voltage = integer("voltage")
    val current = integer("current")

    override val primaryKey: PrimaryKey = PrimaryKey(id)
}

class ChargeTrackingEntity(id: EntityID<Instant>) : Entity<Instant>(id) {
    companion object : EntityClass<Instant, ChargeTrackingEntity>(ChargeTrackings)

    var timestamp by ChargeTrackings.id
    var chargeRecordEntity by ChargeRecordEntity referencedOn ChargeTrackings.idChargeRecord
    var capacity by ChargeTrackings.capacity
    var voltage by ChargeTrackings.voltage
    var current by ChargeTrackings.current
}