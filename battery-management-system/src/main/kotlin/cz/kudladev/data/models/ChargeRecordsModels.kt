package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp


@Serializable
data class ChargeRecord(
    val idChargeRecord: Int? = null,
    val group_id : Int,
    val checked: Boolean,
    val slot: Int,
    @Serializable(with = TimestampSerializer::class) val startedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finishedAt: Timestamp? = null,
    val initialCapacity: Int,
    val chargedCapacity: Int? = null,
    val dischargedCapacity: Int? = null,
    val charger: Charger,
    val battery: Battery
)


@Serializable
data class ChargeRecordInsert(
    val slot: Int,
    val charger_id: Int,
    val battery_id: String
)

@Serializable
data class ChargeRecordWithTracking(
    val idChargeRecord: Int? = null,
    val group_id: Int,
    val checked: Boolean,
    val slot: Int,
    @Serializable(with = TimestampSerializer::class) val startedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finishedAt: Timestamp? = null,
    val initialCapacity: Int,
    val chargedCapacity: Int? = null,
    val dischargedCapacity: Int? = null,
    val charger: Charger,
    val battery: BatteryFormated,
    val tracking: List<FormatedChargeTracking>,
    val cells: List<CellWithFormatedTracking> = emptyList()
)


@Serializable
data class ChargeRecordWithTrackingFormated(
    val idChargeRecord: Int? = null,
    val group_id: Int,
    val checked: Boolean,
    val slot: Int,
    @Serializable(with = TimestampSerializer::class) val startedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finishedAt: Timestamp? = null,
    val initialCapacity: Float,
    val chargedCapacity: Float? = null,
    val dischargedCapacity: Float? = null,
    val charger: Charger,
    val battery: BatteryFormated,
    val tracking: List<FormatedChargeTracking>,
    val cells: List<CellWithFormatedTracking> = emptyList()
)

@Serializable
data class EndOfCharging(
    val type: String,
    val charge_record_id: Int
)