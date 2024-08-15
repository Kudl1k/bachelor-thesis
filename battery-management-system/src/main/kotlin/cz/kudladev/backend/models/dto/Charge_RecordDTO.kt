package cz.kudladev.backend.models.dto

import cz.kudladev.backend.utils.TimestampSerializer
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class Charge_RecordDTO(
    val id_charge_record: Int,
    val program: Char,
    @Serializable(with = TimestampSerializer::class) val started_at: Timestamp,
    @Serializable(with = TimestampSerializer::class) val finished_at: Timestamp,
    val charged_capacity: Int,
    val charger: ChargerDTO,
    val battery: BatteryDTO
)
