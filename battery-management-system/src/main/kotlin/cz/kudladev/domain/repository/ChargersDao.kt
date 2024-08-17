package cz.kudladev.domain.repository

import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.ChargerWithTypes

interface ChargersDao {

    suspend fun getAllChargers(): List<ChargerWithTypes>

    suspend fun getChargerById(id: Int): ChargerWithTypes?

    suspend fun createCharger(charger: Charger): Charger?

    suspend fun updateCharger(charger: Charger): Charger?

    suspend fun deleteCharger(id: Int): Boolean

    suspend fun getChargersByType(typeId: Int): List<ChargerWithTypes>

    suspend fun addTypeToCharger(chargerId: Int, typeId: Int): ChargerWithTypes?

    suspend fun removeTypeFromCharger(chargerId: Int, typeId: Int): ChargerWithTypes?

}