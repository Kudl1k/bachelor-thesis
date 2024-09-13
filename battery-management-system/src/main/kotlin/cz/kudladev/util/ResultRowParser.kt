package cz.kudladev.util

import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.CellModel
import cz.kudladev.data.models.Size
import cz.kudladev.data.models.Type
import org.jetbrains.exposed.sql.ResultRow
import java.sql.Timestamp

object ResultRowParser {

    fun resultRowToType(row: ResultRow): Type {
        return Type(
            shortcut = row[Types.id].value,
            name = row[Types.name]
        )
    }

    fun resultRowToBattery(row: ResultRow): Battery {
        return Battery(
            id = row[Batteries.id].value,
            type = Type(
                shortcut = row[Types.id].value,
                name = row[Types.name]
            ),
            cells = row[Batteries.cells],
            size = Size(
                name = row[Sizes.name].value
            ),
            factory_capacity = row[Batteries.factoryCapacity],
            voltage = row[Batteries.voltage],
            shop_link = row[Batteries.shop_link],
            last_charged_capacity = row[Batteries.lastChargedCapacity],
            last_time_charged_at = row[Batteries.lastTimeChargedAt]?.let { Timestamp.from(it) },
            archived = row[Batteries.archived],
            created_at = Timestamp.from(row[Batteries.createdAt])
        )
    }

    fun resultRowToSize(row: ResultRow): Size {
        return Size(
            name = row[Sizes.name].value,
        )
    }

    fun resultRowToCell(row: ResultRow): CellModel {
        return CellModel(
            idChargeRecord = row[Cell.idChargeRecord],
            number = row[Cell.number]
        )
    }

    fun resultRowToFormatedCellTracking(row: ResultRow): FormatedCellTracking {
        return FormatedCellTracking(
            timestamp = Timestamp.from(row[CellTracking.timestamp]),
            idChargeRecord = row[CellTracking.idChargeRecord],
            number = row[CellTracking.number],
            voltage = convertVoltageToVolt(row[CellTracking.voltage])
        )
    }
}