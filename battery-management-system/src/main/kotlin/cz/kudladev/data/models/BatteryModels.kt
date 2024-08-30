package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Time
import java.sql.Timestamp


@Serializable
data class Battery(
    val id: Int? = null,
    val type: Type,
    val size: Size,
    val factory_capacity: Int,
    val voltage: Int,
    val shop_link: String?,
    val last_charged_capacity: Int?,
    @Serializable(with = TimestampSerializer::class) val last_time_charged_at: Timestamp?,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp?
)

@Serializable
data class BatteryFormated(
    val id: Int,
    val type: Type,
    val size: Size,
    val factory_capacity: Int,
    val voltage: Float,
    val shop_link: String?,
    val last_charged_capacity: Float?,
    @Serializable(with = TimestampSerializer::class) val last_time_charged_at: Timestamp?,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp?
)


@Serializable
data class BatteryInsert(
    val type: String,
    val size: String,
    val factory_capacity: Int,
    val shop_link: String?,
    val voltage: Int
)

@Serializable
data class BatteryWithSlot(
    val id: Int,
    val slot: Int,
)

@Serializable
data class BatteryInfo(
    val id: Int,
    val type: Type,
    val size: Size,
    val factory_capacity: Int,
    val voltage: Float,
    val shop_link: String?,
    val last_charged_capacity: Float?,
    @Serializable(with = TimestampSerializer::class) val last_time_charged_at: Timestamp?,
    @Serializable(with = TimestampSerializer::class) val created_at: Timestamp?,
    val charge_records: List<ChargeRecordWithTrackingFormated>
)