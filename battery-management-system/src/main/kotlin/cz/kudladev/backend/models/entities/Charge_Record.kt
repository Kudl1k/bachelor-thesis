package cz.kudladev.backend.models.entities

import java.sql.Timestamp

data class Charge_Record(
    val id_charge_record: Int? = null,
    val program: Char,
    val started_at: Timestamp,
    val finished_at: Timestamp,
    val charged_capacity: Int,
    val id_charger: Int,
    val id_battery: Int,
)
