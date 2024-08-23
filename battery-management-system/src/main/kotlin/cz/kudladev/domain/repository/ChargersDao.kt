package cz.kudladev.domain.repository

import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerInsert
import cz.kudladev.data.models.ChargerWithTypesAndSizes
import cz.kudladev.data.models.SearchCharger

interface ChargersDao {

    suspend fun getAllChargers(): List<ChargerWithTypesAndSizes>

    suspend fun getChargerById(id: Int): ChargerWithTypesAndSizes?

    suspend fun getChargerByTypesAndSizes(searchCharger: SearchCharger): List<ChargerWithTypesAndSizes>

    suspend fun createCharger(charger: ChargerInsert): ChargerWithTypesAndSizes?

    suspend fun updateCharger(charger: Charger): Charger?

    suspend fun deleteCharger(id: Int): Boolean

    suspend fun getChargersByType(shortcut: String): List<ChargerWithTypesAndSizes>

    suspend fun addTypeToCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes?

    suspend fun removeTypeFromCharger(chargerId: Int, shortcut: String): ChargerWithTypesAndSizes?

    suspend fun addSizeToCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes?

    suspend fun removeSizeFromCharger(chargerId: Int, size: String): ChargerWithTypesAndSizes?


}