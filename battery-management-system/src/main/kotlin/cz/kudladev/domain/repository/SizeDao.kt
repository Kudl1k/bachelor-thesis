package cz.kudladev.domain.repository

import cz.kudladev.data.models.Size
import cz.kudladev.data.models.SizeWithBatteries

interface SizeDao {
    suspend fun getAllSizes(): List<Size>
    suspend fun getSizeByName(name: String): Size?
    suspend fun getSizeByNameWithBatteries(name: String): SizeWithBatteries?
    suspend fun insertSize(name: String): Size?
    suspend fun deleteSize(name: String): Boolean
}