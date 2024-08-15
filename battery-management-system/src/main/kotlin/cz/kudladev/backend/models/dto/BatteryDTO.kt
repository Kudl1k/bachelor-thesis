package cz.kudladev.backend.models.dto

import cz.kudladev.backend.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class BatteryDTO(
    val id_battery: Int,
    val type: TypeDTO,
    val size: String,
    val factory_capacity: Int,
    val voltage: Int,
    val last_charged_capacity: Int?,
    @Serializable(with = TimestampSerializer::class) val last_charged_at: Timestamp?,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp
)
