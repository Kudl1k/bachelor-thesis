package cz.kudladev.data.models

import kotlinx.serialization.Serializable


@Serializable
data class Type(
    val id: Int? = null,
    val shortcut: String,
    val name: String
)

@Serializable
data class TypeBatteries(
    val id: Int? = null,
    val shortcut: String,
    val name: String,
    val batteries: List<Battery>
)

