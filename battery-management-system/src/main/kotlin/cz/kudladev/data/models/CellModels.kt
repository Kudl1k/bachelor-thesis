package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp


@Serializable
data class CellModel(
    val idChargeRecord: Int,
    val number: Int,
)

@Serializable
data class CellWithTracking(
    val idChargeRecord: Int,
    val number: Int,
    val voltages: List<CellTrackingModel>
)

@Serializable
data class CellWithFormatedTracking(
    val idChargeRecord: Int,
    val number: Int,
    val voltages: List<FormatedCellTracking>
)

@Serializable
data class CellTrackingModel(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val idChargeRecord: Int,
    val number: Int,
    val voltage: Int,
)

@Serializable
data class FormatedCellTracking(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val idChargeRecord: Int,
    val number: Int,
    val voltage: Float,
)