package cz.kudladev.data.models

import cz.kudladev.util.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp


@Serializable
data class Cell(
    @Serializable(with = TimestampSerializer::class) val timestamp: Timestamp,
    val idChargeRecord: Int,
    val number: Int,
    val voltage: Float
)