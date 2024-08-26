package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object Sizes : IdTable<String>("size") {
    val name = varchar("name", 50).entityId()
    override val id: Column<EntityID<String>> = name
    override val primaryKey = PrimaryKey(name)
}

class SizeEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, SizeEntity>(Sizes)

    var name by Sizes.name
}