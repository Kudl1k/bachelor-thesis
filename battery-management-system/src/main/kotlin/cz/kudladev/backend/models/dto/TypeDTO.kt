package cz.kudladev.backend.models.dto

import kotlinx.serialization.Serializable


@Serializable
data class TypeDTO(
    val id_type: Int,
    val shortcut: String,
    val name: String,
    val chargers: List<ChargerDTO> = emptyList(),
)
