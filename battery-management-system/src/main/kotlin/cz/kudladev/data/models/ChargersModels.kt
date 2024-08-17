package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Charger(
    val id: Int? = null,
    val name: String,
    val tty: String,
    val slots: Int,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp? = null,
)


@Serializable
data class ChargerWithTypes(
    val id: Int? = null,
    val name: String,
    val tty: String,
    val slots: Int,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp,
    val types: List<Type?> = emptyList()
)