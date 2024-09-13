package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.javatime.timestamp


object Batteries : IdTable<String>("battery") {
    override val id = varchar("id", 8).entityId()
    val typeShortcut = reference("shortcut", Types) // Use reference for foreign key
    val size = reference("size", Sizes) // Use reference for foreign key
    val cells = integer("cells").default(1)
    val factoryCapacity = integer("factory_capacity")
    val voltage = integer("voltage")
    val shop_link = varchar("shop_link", 255).nullable()
    val lastChargedCapacity = integer("last_charged_capacity").nullable()
    val lastTimeChargedAt = timestamp("last_time_charged_at").nullable()
    val archived = bool("archived").default(false)
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}

class BatteryEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String,BatteryEntity>(Batteries)

    var typeEntity by TypeEntity referencedOn Batteries.typeShortcut
    var sizeEntity by SizeEntity referencedOn Batteries.size
    var cells by Batteries.cells
    var factoryCapacity by Batteries.factoryCapacity
    var voltage by Batteries.voltage
    var shopLink by Batteries.shop_link
    var lastChargedCapacity by Batteries.lastChargedCapacity
    var lastTimeChargedAt by Batteries.lastTimeChargedAt
    var archived by Batteries.archived
    var createdAt by Batteries.createdAt
}