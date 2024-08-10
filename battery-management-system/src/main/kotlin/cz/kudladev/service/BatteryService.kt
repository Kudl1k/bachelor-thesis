package cz.kudladev.service

import java.sql.Connection


//TODO: Implement BatteryService
class BatteryService(private val connection: Connection) {
    companion object {
        //TODO: Implement CREATE_TABLE_BATTERIES
    }

    init {
        val statement = connection.createStatement()
        //statement.executeUpdate(CREATE_TABLE_BATTERIES)
    }

}