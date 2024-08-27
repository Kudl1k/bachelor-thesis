package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp
import java.text.Normalizer.Form


@Serializable
data class ChargeRecord(
    val idChargeRecord: Int? = null,
    val program: String,
    val slot: Int,
    @Serializable(with = TimestampSerializer::class) val startedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finishedAt: Timestamp? = null,
    val chargedCapacity: Int? = null,
    val charger: Charger,
    val battery: Battery
)


@Serializable
data class ChargeRecordInsert(
    val program: String,
    val slot: Int,
    val charger_id: Int,
    val battery_id: Int
)

@Serializable
data class ChargeRecordWithTracking(
    val idChargeRecord: Int? = null,
    val program: String,
    val slot: Int,
    @Serializable(with = TimestampSerializer::class) val startedAt: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finishedAt: Timestamp? = null,
    val chargedCapacity: Int? = null,
    val charger: Charger,
    val battery: Battery,
    val tracking: List<FormatedChargeTracking>
)