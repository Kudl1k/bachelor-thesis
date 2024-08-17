package cz.kudladev.util

import cz.kudladev.data.Batteries
import cz.kudladev.data.ChargerTypes
import cz.kudladev.data.Chargers
import cz.kudladev.data.Types
import cz.kudladev.data.models.*
import org.jetbrains.exposed.sql.ResultRow
import java.sql.Timestamp

object ResultRowParser {

    fun resultRowToType(row: ResultRow): Type {
        return Type(
            id = row[Types.idType],
            shortcut = row[Types.shortcut] ?: "",
            name = row[Types.name] ?: ""
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
            last_time_charged_at = row[Batteries.lastTimeChargedAt]?.let { Timestamp.from(it) },
            created_at = Timestamp.from(row[Batteries.createdAt])
        )
    }

    fun resultRowToCharger(row: ResultRow): Charger {
        return Charger(
            id = row[Chargers.idCharger],
            name = row[Chargers.name],
            tty = row[Chargers.tty],
            slots = row[Chargers.slots],
            created_at = Timestamp.from(row[Chargers.createdAt])
        )
    }

    fun resultRowToChargerWithTypes(rows: List<ResultRow>): ChargerWithTypes {
        val firstRow = rows.first()
        val charger = resultRowToCharger(firstRow)
        val types = rows.mapNotNull { row ->
            if (row[Types.idType] != null) {
                resultRowToType(row)
            } else {
                null
            }
        }
        return ChargerWithTypes(
            id = charger.id,
            name = charger.name,
            tty = charger.tty,
            slots = charger.slots,
            created_at = charger.created_at!!,
            types = types
        )
    }

}