package cz.kudladev.util

import cz.kudladev.data.Batteries
import cz.kudladev.data.Types
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.Type
import cz.kudladev.data.models.TypeBatteries
import org.jetbrains.exposed.sql.ResultRow
import java.sql.Timestamp

object ResultRowParser {

    fun resultRowToType(row: ResultRow): Type {
        return Type(
            id = row[Types.idType],
            shortcut = row[Types.shortcut],
            name = row[Types.name]
        )
    }

    fun resultRowToBattery(row: ResultRow): Battery {
        return Battery(
            id = row[Batteries.idBattery],
            type = Type(
                id = row[Types.idType],
                shortcut = row[Types.shortcut],
                name = row[Types.name]
            ),
            size = row[Batteries.size],
            factory_capacity = row[Batteries.factoryCapacity],
            voltage = row[Batteries.voltage],
            last_charged_capacity = row[Batteries.lastChargedCapacity],
            last_time_charged_at = if (row[Batteries.lastTimeChargedAt] == null) null else Timestamp.from(row[Batteries.lastTimeChargedAt]),
            created_at = Timestamp.from(row[Batteries.createdAt])
        )
    }

}