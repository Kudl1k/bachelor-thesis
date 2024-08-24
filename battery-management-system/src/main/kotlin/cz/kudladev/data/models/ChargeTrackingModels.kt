package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class ChargeTrackingID(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp? = null,
    val charge_record_id: Int,
    val capacity: Int,
    val voltage: Int,
    val current: Int
)

@Serializable
data class StartChargeTracking(
    val id_charger: Int,
    val batteries: List<BatteryWithSlot>,
)