package cz.kudladev.backend.models.entities

import java.sql.Timestamp

data class Battery(
    val id_battery: Int? = null,
    val id_type: Int,
    val size: String,
    val factory_capacity: Int,
    val voltage: Int,
    val last_charged_capacity: Int?,
    val last_charged_at: Timestamp?,
    val created_at: Timestamp
)
