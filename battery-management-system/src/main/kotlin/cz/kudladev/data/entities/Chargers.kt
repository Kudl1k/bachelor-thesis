package cz.kudladev.data.entities

import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

object Chargers : IntIdTable("charger") {
    val name = varchar("name", 50)
    val parser = reference("parserId", Parser)
    val tty = varchar("tty", 30)
    val baudRate = integer("baud_rate")
    val dataBits = integer("data_bits")
    val stopBits = integer("stop_bits")
    val parity = integer("parity")
    val rts = bool("rts")
    val dtr = bool("dtr")
    val slots = integer("slots")
    val createdAt = timestamp("created_at")
}

class ChargerEntity(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<ChargerEntity>(Chargers)

    var name by Chargers.name
    var parser by ParserEntity referencedOn Chargers.parser
    var tty by Chargers.tty
    var baudRate by Chargers.baudRate
    var dataBits by Chargers.dataBits
    var stopBits by Chargers.stopBits
    var parity by Chargers.parity
    var rts by Chargers.rts
    var dtr by Chargers.dtr
    var slots by Chargers.slots
    var createdAt by Chargers.createdAt
}