package cz.kudladev.domain.repository

import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerWithTypesAndSizes

interface ChargersDao {

    suspend fun getAllChargers(): List<ChargerWithTypesAndSizes>

    suspend fun getChargerById(id: Int): ChargerWithTypesAndSizes?

    suspend fun createCharger(charger: Charger): Charger?

    suspend fun updateCharger(charger: Charger): Charger?

    suspend fun deleteCharger(id: Int): Boolean

    suspend fun getChargersByType(shortcut: String): List<ChargerWithTypesAndSizes>

    suspend fun addTypeToCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes?

    suspend fun removeTypeFromCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes?

    suspend fun addSizeToCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes?

    suspend fun removeSizeFromCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes?


}