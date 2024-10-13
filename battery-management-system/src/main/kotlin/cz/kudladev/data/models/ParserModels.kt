package cz.kudladev.data.models

import kotlinx.serialization.Serializable

@Serializable
data class ParserModel(
    val id: Int,
    val name: String
)