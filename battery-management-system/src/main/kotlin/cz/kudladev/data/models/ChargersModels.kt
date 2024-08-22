package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Charger(
    val id: Int? = null,
    val name: String,
    val tty: String,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int,
    val rts: Boolean,
    val dtr: Boolean,
    val slots: Int,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp? = null,
)


@Serializable
data class ChargerWithTypesAndSizes(
    val id: Int? = null,
    val name: String,
    val tty: String,
    val baudRate: Int,
    val dataBits: Int,
    val stopBits: Int,
    val parity: Int,
    val rts: Boolean,
    val dtr: Boolean,
    val slots: Int,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp,
    val types: List<Type?> = emptyList(),
    val sizes: List<Size?> = emptyList()
)