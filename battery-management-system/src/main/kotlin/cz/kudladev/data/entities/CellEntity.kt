package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp
import java.time.Instant


object CellTracking : Table("cell_tracking") {
    val timestamp = timestamp("timestamp").references(ChargeTrackings.id)
    val idChargeRecord = integer("id_charge_record").references(ChargeRecords.id)
    val number = integer("number")
    val voltage = float("voltage")

    override val primaryKey = PrimaryKey(timestamp, idChargeRecord, number)
}