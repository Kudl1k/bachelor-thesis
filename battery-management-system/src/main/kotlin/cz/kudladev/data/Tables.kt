package cz.kudladev.data

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp


// Type Table
object Types : Table("type") {
    val idType = integer("id_type").autoIncrement()
    val shortcut = varchar("shortcut", 10)
    val name = varchar("name", 50)

    override val primaryKey = PrimaryKey(idType)
}

// Battery Table
object Batteries : Table("battery") {
    val idBattery = integer("id_battery").autoIncrement()
    val idType = integer("id_type").references(Types.idType)
    val size = varchar("size", 10)
    val factoryCapacity = integer("factory_capacity")
    val voltage = integer("voltage")
    val lastChargedCapacity = integer("last_charged_capacity").nullable()
    val lastTimeChargedAt = timestamp("last_time_charged_at").nullable()
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(idBattery)
}

// Charger Table
object Chargers : Table("charger") {
    val idCharger = integer("id_charger").autoIncrement()
    val name = varchar("name", 50)
    val tty = varchar("tty", 15)
    val slots = integer("slots")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(idCharger)
}

// Charger_Type Table
object ChargerTypes : Table("charger_type") {
    val idCharger = integer("id_charger").references(Chargers.idCharger)
    val idType = integer("id_type").references(Types.idType)

    override val primaryKey = PrimaryKey(idCharger, idType)
}

// Charge_Record Table
object ChargeRecords : Table("charge_record") {
    val idChargeRecord = integer("id_charge_record").autoIncrement()
    val program = varchar("program", 1) // Assuming CHAR is stored as a single-character VARCHAR
    val slot = integer("slot")
    val startedAt = timestamp("started_at")
    val finishedAt = timestamp("finished_at").nullable()
    val chargedCapacity = integer("charged_capacity").nullable()
    val idCharger = integer("id_charger").references(Chargers.idCharger)
    val idBattery = integer("id_battery").references(Batteries.idBattery)

    override val primaryKey = PrimaryKey(idChargeRecord)
}

// Charge_Tracking Table
object ChargeTracking : Table("charge_tracking") {
    val timestamp = timestamp("timestamp")
    val idChargeRecord = integer("id_charge_record").references(ChargeRecords.idChargeRecord)
    val capacity = integer("capacity")
    val voltage = integer("voltage")
    val current = integer("current")

    override val primaryKey = PrimaryKey(timestamp)
}
