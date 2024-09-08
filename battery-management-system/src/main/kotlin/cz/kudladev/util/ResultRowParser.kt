package cz.kudladev.util

import cz.kudladev.data.*
import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import cz.kudladev.data.models.Battery
import cz.kudladev.data.models.ChargeRecord
import cz.kudladev.data.models.Charger
import cz.kudladev.data.models.Size
import cz.kudladev.data.models.Type
import org.jetbrains.exposed.sql.ResultRow
import java.sql.Timestamp

object ResultRowParser {

    fun resultRowToType(row: ResultRow): Type {
        return Type(
            shortcut = row[Types.id].value,
            name = row[Types.name] ?: ""
        )
    }

    fun resultRowToBattery(row: ResultRow): Battery {
        return Battery(
            id = row[Batteries.id].value,
            type = Type(
                shortcut = row[Types.id].value,
                name = row[Types.name]
            ),
            size = Size(
                name = row[Sizes.name].value
            ),
            factory_capacity = row[Batteries.factoryCapacity],
            voltage = row[Batteries.voltage],
            shop_link = row[Batteries.shop_link],
            last_charged_capacity = row[Batteries.lastChargedCapacity],
            last_time_charged_at = row[Batteries.lastTimeChargedAt]?.let { Timestamp.from(it) },
            created_at = Timestamp.from(row[Batteries.createdAt])
        )
    }

    fun resultRowToCharger(row: ResultRow): Charger {
        return Charger(
            id = row[Chargers.id].value,
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

    fun resultRowToChargerRecord(charger: Charger,battery: Battery,row: ResultRow): ChargeRecord {
        return ChargeRecord(
            idChargeRecord = row[ChargeRecords.id].value,
            slot = row[ChargeRecords.slot],
            startedAt = Timestamp.from(row[ChargeRecords.startedAt]),
            finishedAt = row[ChargeRecords.finishedAt]?.let { Timestamp.from(it) },
            initialCapacity = row[ChargeRecords.initialCapacity],
            chargedCapacity = row[ChargeRecords.chargedCapacity],
            charger = charger,
            battery = battery
        )
    }

    fun resultRowToChargerRecordWithTracking(charger: Charger,battery: BatteryFormated,row: ResultRow, tracking: List<FormatedChargeTracking>): ChargeRecordWithTracking {
        return ChargeRecordWithTracking(
            idChargeRecord = row[ChargeRecords.id].value,
            slot = row[ChargeRecords.slot],
            startedAt = Timestamp.from(row[ChargeRecords.startedAt]),
            finishedAt = row[ChargeRecords.finishedAt]?.let { Timestamp.from(it) },
            initialCapacity = row[ChargeRecords.initialCapacity],
            chargedCapacity = row[ChargeRecords.chargedCapacity],
            charger = charger,
            battery = battery,
            tracking = tracking
        )
    }

    fun resultRowToChargeTracking(row: ResultRow): ChargeTrackingID{
        return ChargeTrackingID(
            timestamp = Timestamp.from(row[ChargeTrackings.id].value),
            charge_record_id = row[ChargeTrackings.idChargeRecord].value,
            charging = row[ChargeTrackings.charging],
            real_capacity = row[ChargeTrackings.realCapacity],
            capacity = row[ChargeTrackings.capacity],
            voltage = row[ChargeTrackings.voltage],
            current = row[ChargeTrackings.current]
        )
    }

    fun resultRowToSize(row: ResultRow): Size {
        return Size(
            name = row[Sizes.name].value,
        )
    }

}