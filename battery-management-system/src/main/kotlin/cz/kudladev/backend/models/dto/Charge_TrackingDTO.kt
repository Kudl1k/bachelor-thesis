package cz.kudladev.backend.models.dto

import cz.kudladev.backend.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Charge_TrackingDTO(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val Charge_Record: Charge_RecordDTO,
    val capacity: Int,
    val voltage: Int,
    val current: Int
)
