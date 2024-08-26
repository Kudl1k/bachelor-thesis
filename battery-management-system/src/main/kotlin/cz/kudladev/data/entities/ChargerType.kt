package cz.kudladev.data.entities

import cz.kudladev.data.entities.ChargeRecords.integer
import cz.kudladev.data.entities.ChargeRecords.varchar
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

// Charger_Type Table
object ChargerTypes : Table("charger_type") {
    val idCharger = reference("id_charger", Chargers)
    val typeShortcut = reference("type_shortcut", Types.shortcut)

    override val primaryKey = PrimaryKey(idCharger, typeShortcut)
}
