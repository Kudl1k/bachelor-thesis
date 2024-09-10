package cz.kudladev.domain.repository

import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.BatteryFormated
import cz.kudladev.data.models.BatteryInfo
import cz.kudladev.data.models.BatteryInsert


interface BatteriesDao {

    suspend fun getAllBatteries(): List<BatteryFormated>

    suspend fun getBatteryById(id: String): BatteryFormated?

    suspend fun createBattery(battery: BatteryInsert): BatteryFormated?

    suspend fun updateBattery(battery: Battery): BatteryFormated?

    suspend fun deleteBattery(id: String): BatteryFormated?

    suspend fun updateBatteryLastChargingCapacity(id: String, capacity: Int): BatteryFormated?

    suspend fun getBatteryInfo(id: String): BatteryInfo?

    fun generateBatteryId(): String
}