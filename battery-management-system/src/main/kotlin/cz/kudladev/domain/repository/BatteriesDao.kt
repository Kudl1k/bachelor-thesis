package cz.kudladev.domain.repository

import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.BatteryInsert


interface BatteriesDao {

    suspend fun getAllBatteries(): List<Battery>

    suspend fun getBatteryById(id: Int): Battery?

    suspend fun createBattery(battery: BatteryInsert): Battery?

    suspend fun updateBattery(battery: Battery): Battery?

    suspend fun deleteBattery(id: Int): Battery?

}