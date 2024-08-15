package cz.kudladev.backend.models.dto

import cz.kudladev.backend.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class ChargerDTO(
    val id_charger: Int,
    val name: String,
    val tty: String,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp,
    val types: List<TypeDTO> = emptyList()
)
