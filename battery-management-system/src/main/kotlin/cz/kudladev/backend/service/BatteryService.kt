package cz.kudladev.backend.service

import cz.kudladev.backend.models.entities.Battery
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection


//TODO: Implement BatteryService
class BatteryService(private val connection: Connection) {
    companion object {
        private const val SELECT_ALL_BATTERIES = "SELECT * FROM batteries"
        private const val SELECT_BATTERY_BY_ID = "SELECT * FROM batteries WHERE id = ?"
        private const val INSERT_BATTERY = "INSERT INTO batteries (size, factory_capacity, voltage, created_at, updated_at) VALUES (?, ?, ?, ?, ?)"
    }

    suspend fun getAllBatteries() = withContext(Dispatchers.IO) {
        val statement = connection.createStatement()
        val resultSet = statement.executeQuery(SELECT_ALL_BATTERIES)
        val batteries = mutableListOf<Battery>()
        while (resultSet.next()) {
            val id_battery = resultSet.getInt("id_battery")
            val id_type = resultSet.getInt("id_type")
            val size = resultSet.getString("size")
            val factory_capacity = resultSet.getInt("factory_capacity")
            val voltage = resultSet.getInt("voltage")
            val last_charged_capacity = resultSet.getInt("last_charged_capacity")
            val last_charged_at = resultSet.getTimestamp("last_charged_at")
            val created_at = resultSet.getTimestamp("created_at")
            batteries.add(
                Battery(
                    id_battery = id_battery,
                    id_type = id_type,
                    size = size,
                    factory_capacity = factory_capacity,
                    voltage = voltage,
                    last_charged_capacity = last_charged_capacity,
                    last_charged_at = last_charged_at,
                    created_at = created_at
                )
            )
        }
        return@withContext batteries
    }



}