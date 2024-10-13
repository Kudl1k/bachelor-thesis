package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable

object Parser : IntIdTable("parser") {
    val name = varchar("name", 255)
}

class ParserEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ParserEntity>(Parser)

    var name by Parser.name
}