package cz.kudladev.util

import cz.kudladev.data.*
import cz.kudladev.data.models.*
import org.jetbrains.exposed.sql.ResultRow
import java.sql.Timestamp

object ResultRowParser {

    fun resultRowToType(row: ResultRow): Type {
        return Type(
            shortcut = row[Types.shortcut],
            name = row[Types.name] ?: ""
        )
    }

    fun resultRowToBattery(row: ResultRow): Battery {
        return Battery(
            id = row[Batteries.idBattery],
            type = Type(
                shortcut = row[Types.shortcut],
                name = row[Types.name]
            ),
            size = Size(
                name = row[Sizes.name]
            ),
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
            baudRate = row[Chargers.baudRate],
            dataBits = row[Chargers.dataBits],
            stopBits = row[Chargers.stopBits],
            parity = row[Chargers.parity],
            rts = row[Chargers.rts],
            dtr = row[Chargers.dtr],
            slots = row[Chargers.slots],
            created_at = Timestamp.from(row[Chargers.createdAt])
        )
    }

    fun resultRowToChargerWithTypes(rows: List<ResultRow>): ChargerWithTypesAndSizes {
        val firstRow = rows.first()
        val charger = resultRowToCharger(firstRow)
        val types = rows.mapNotNull { row ->
            if (row[Types.shortcut] != null) {
                resultRowToType(row)
            } else {
                null
            }
        }
        val sizes = rows.mapNotNull { row ->
            if (row[Sizes.name] != null) {
                resultRowToSize(row)
            } else {
                null
            }
        }.toSet()


        return ChargerWithTypesAndSizes(
            id = charger.id,
            name = charger.name,
            tty = charger.tty,
            baudRate = charger.baudRate,
            dataBits = charger.dataBits,
            stopBits = charger.stopBits,
            parity = charger.parity,
            rts = charger.rts,
            dtr = charger.dtr,
            slots = charger.slots,
            created_at = charger.created_at!!,
            types = types,
            sizes = sizes
        )
    }

    fun resultRowToChargerRecord(row: ResultRow): ChargeRecord {
        val charger = resultRowToCharger(row)
        println(charger)
        val battery = resultRowToBattery(row)
        println(battery)

        return ChargeRecord(
            idChargeRecord = row[ChargeRecords.idChargeRecord],
            program = row[ChargeRecords.program],
            slot = row[ChargeRecords.slot],
            startedAt = Timestamp.from(row[ChargeRecords.startedAt]),
            finishedAt = row[ChargeRecords.finishedAt]?.let { Timestamp.from(it) },
            chargedCapacity = row[ChargeRecords.chargedCapacity],
            charger = charger,
            battery = battery
        )
    }

    fun resultRowToChargeTracking(row: ResultRow): ChargeTrackingID{
        return ChargeTrackingID(
            timestamp = Timestamp.from(row[ChargeTracking.timestamp]),
            charge_record_id = row[ChargeTracking.idChargeRecord],
            capacity = row[ChargeTracking.capacity],
            voltage = row[ChargeTracking.voltage],
            current = row[ChargeTracking.current]
        )
    }

    fun resultRowToSize(row: ResultRow): Size {
        return Size(
            name = row[Sizes.name],
        )
    }

}