package cz.kudladev.data.models

import kotlinx.serialization.Serializable


@Serializable
data class Size(
    val name: String
)

@Serializable
data class SizeWithBatteries(
    val name: String,
    val batteries: List<Battery>
)