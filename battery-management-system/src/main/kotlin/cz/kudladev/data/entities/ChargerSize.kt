package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object ChargerSizes : Table("charger_size") {
    val idCharger = integer("id_charger").references(Chargers.id)
    val sizeName = varchar("name", 20).references(Sizes.name)

    override val primaryKey = PrimaryKey(idCharger, sizeName)
}