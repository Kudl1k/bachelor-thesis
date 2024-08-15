package cz.kudladev.backend.models.entities

import java.sql.Timestamp

data class Charge_Tracking(
    val timestamp: Timestamp,
    val id_charge_record: Int,
    val id_battery: Int,
    val capacity: Int,
    val voltage: Int,
    val current: Int
)
