package cz.kudladev.data.entities

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp


object Cell : Table("cell") {
    val idChargeRecord = integer("id_charge_record").references(ChargeRecords.id)
    val number = integer("number")

    override val primaryKey = PrimaryKey(idChargeRecord, number)
}

object CellTracking : Table("cell_tracking") {
    val timestamp = timestamp("timestamp")
    val idChargeRecord = integer("id_charge_record").references(ChargeRecords.id)
    val number = integer("number")
    val voltage = integer("voltage")

    override val primaryKey = PrimaryKey(timestamp, idChargeRecord, number)
}