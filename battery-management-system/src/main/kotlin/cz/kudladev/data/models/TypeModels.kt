package cz.kudladev.data.models

import kotlinx.serialization.Serializable


@Serializable
data class Type(
    val shortcut: String,
    val name: String
)

@Serializable
data class TypeBatteries(
    val shortcut: String,
    val name: String,
    val batteries: List<Battery>
)

