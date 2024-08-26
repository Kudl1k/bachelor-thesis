package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp


object Batteries : IntIdTable("battery") {
    val typeShortcut = reference("shortcut", Types) // Use reference for foreign key
    val size = reference("size", Sizes) // Use reference for foreign key
    val factoryCapacity = integer("factory_capacity")
    val voltage = integer("voltage")
    val lastChargedCapacity = integer("last_charged_capacity").nullable()
    val lastTimeChargedAt = timestamp("last_time_charged_at").nullable()
    val createdAt = timestamp("created_at")
}

class BatteryEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<BatteryEntity>(Batteries)

    var typeEntity by TypeEntity referencedOn Batteries.typeShortcut
    var sizeEntity by SizeEntity referencedOn Batteries.size
    var factoryCapacity by Batteries.factoryCapacity
    var voltage by Batteries.voltage
    var lastChargedCapacity by Batteries.lastChargedCapacity
    var lastTimeChargedAt by Batteries.lastTimeChargedAt
    var createdAt by Batteries.createdAt
}