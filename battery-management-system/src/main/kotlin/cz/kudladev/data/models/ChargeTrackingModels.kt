package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class ChargeTrackingID(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val charge_record_id: Int,
    val capacity: Int,
    val voltage: Int,
    val current: Int
)