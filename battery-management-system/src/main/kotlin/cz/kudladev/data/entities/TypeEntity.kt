package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.Entity
import org.jetbrains.exposed.dao.EntityClass
import org.jetbrains.exposed.dao.id.IdTable
import org.jetbrains.exposed.sql.Column

object Types : IdTable<String>("type") {
    val shortcut = varchar("shortcut", 10).entityId()
    val name = varchar("name", 50)
    override val id: Column<EntityID<String>> = shortcut
    override val primaryKey = PrimaryKey(shortcut)
}

class TypeEntity(id: EntityID<String>) : Entity<String>(id) {
    companion object : EntityClass<String, TypeEntity>(Types)

    var shortcut by Types.shortcut
    var name by Types.name
}
