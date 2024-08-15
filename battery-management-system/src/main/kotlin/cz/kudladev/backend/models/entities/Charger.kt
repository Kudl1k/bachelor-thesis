package cz.kudladev.backend.models.entities

import java.sql.Timestamp

data class Charger(
    val id_charger: Int? = null,
    val name: String,
    val tty: String,
    val created_at: Timestamp
)