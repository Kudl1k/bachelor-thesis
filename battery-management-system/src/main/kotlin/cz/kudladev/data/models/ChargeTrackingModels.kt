package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class ChargeTrackingID(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp? = null,
    val charge_record_id: Int,
    val charging: Boolean,
    val real_capacity: Int,
    val capacity: Int,
    val voltage: Int,
    val current: Int,
)

@Serializable
data class FormatedChargeTracking(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val charge_record_id: Int,
    val charging: Boolean,
    val real_capacity: Float,
    val capacity: Float,
    val voltage: Float,
    val current: Float,
)

@Serializable
data class StartChargeTracking(
    val id_charger: Int,
    val batteries: List<BatteryWithSlot>,
)

@Serializable
data class ChargeTrackingWithCellTrackings(
    val formatedChargeTracking: FormatedChargeTracking,
    val formatedCellTrackings: List<FormatedCellTracking>,
)

@Serializable
data class ChargeTrackingsWithCellTrackings(
    val formatedChargeTrackings: List<FormatedChargeTracking>,
    val formatedCellTrackings: List<FormatedCellTracking>,
)

fun convertCurrentToAmpere(currentInMilliAmps: Int): Float {
    return currentInMilliAmps / 1000.0f
}

fun convertVoltageToVolt(voltageInMilliVolts: Int): Float {
    return voltageInMilliVolts / 1000.0f
}

fun convertChargedOrDischargedCapacityToMilliAmpHour(chargedOrDischargedCapacityInHundredthsOfMilliAmpHours: Int): Float {
    return chargedOrDischargedCapacityInHundredthsOfMilliAmpHours / 100.0f
}
