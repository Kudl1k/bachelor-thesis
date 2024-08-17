package cz.kudladev.domain.repository

import cz.kudladev.data.models.Battery


interface BatteriesDao {

    suspend fun getAllBatteries(): List<Battery>

    suspend fun getBatteryById(id: Int): Battery?

    suspend fun createBattery(battery: Battery): Battery?

    suspend fun updateBattery(battery: Battery): Battery?

    suspend fun deleteBattery(id: Int): Battery?

}