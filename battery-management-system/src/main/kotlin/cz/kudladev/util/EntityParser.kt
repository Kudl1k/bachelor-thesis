package cz.kudladev.util

import cz.kudladev.data.entities.*
import cz.kudladev.data.models.*
import java.sql.Timestamp

object EntityParser {

    fun toBattery(entity: BatteryEntity, type: Type, size: Size): Battery {
        return Battery(
            id = entity.id.value,
            type = type,
            size = size,
            cells = entity.cells,
            factory_capacity = entity.factoryCapacity,
            voltage = entity.voltage,
            shop_link = entity.shopLink,
            last_charged_capacity = entity.lastChargedCapacity,
            last_time_charged_at = entity.lastTimeChargedAt?.let { Timestamp.from(it) },
            archived = entity.archived,
            created_at = Timestamp.from(entity.createdAt)
        )
    }

    fun toFormatedBattery(entity: BatteryEntity, type: Type, size: Size): BatteryFormated {
        return BatteryFormated(
            id = entity.id.value,
            type = type,
            size = size,
            cells = entity.cells,
            factory_capacity = entity.factoryCapacity,
            voltage = convertVoltageToVolt(entity.voltage),
            shop_link = entity.shopLink,
            last_charged_capacity = entity.lastChargedCapacity?.let { convertChargedOrDischargedCapacityToMilliAmpHour(it) },
            last_time_charged_at = entity.lastTimeChargedAt?.let { Timestamp.from(it) },
            archived = entity.archived,
            created_at = Timestamp.from(entity.createdAt)
        )
    }

    fun toType(entity: TypeEntity): Type {
        return Type(
            shortcut = entity.id.value,
            name = entity.name
        )
    }

    fun toSize(entity: SizeEntity): Size {
        return Size(
            name = entity.id.value
        )
    }

    fun toCharger(chargerEntity: ChargerEntity): Charger{
        return Charger(
            id = chargerEntity.id.value,
            name = chargerEntity.name,
            tty = chargerEntity.tty,
            baudRate = chargerEntity.baudRate,
            dataBits = chargerEntity.dataBits,
            stopBits = chargerEntity.stopBits,
            parity = chargerEntity.parity,
            rts = chargerEntity.rts,
            dtr = chargerEntity.dtr,
            slots = chargerEntity.slots,
            created_at = chargerEntity.createdAt?.let { Timestamp.from(it) }
        )
    }

    fun toChargerWithTypesAndSizes(chargerEntity: ChargerEntity, types: List<Type>, sizes: Set<Size>): ChargerWithTypesAndSizes{
        return ChargerWithTypesAndSizes(
            id = chargerEntity.id.value,
            name = chargerEntity.name,
            tty = chargerEntity.tty,
            baudRate = chargerEntity.baudRate,
            dataBits = chargerEntity.dataBits,
            stopBits = chargerEntity.stopBits,
            parity = chargerEntity.parity,
            rts = chargerEntity.rts,
            dtr = chargerEntity.dtr,
            slots = chargerEntity.slots,
            created_at = Timestamp.from(chargerEntity.createdAt),
            types = types,
            sizes = sizes.toSet()
        )
    }

    fun toChargeRecord(chargeRecordEntity: ChargeRecordEntity, charger: Charger, battery: Battery): ChargeRecord{
        return ChargeRecord(
            idChargeRecord = chargeRecordEntity.id.value,
            slot = chargeRecordEntity.slot,
            startedAt = Timestamp.from(chargeRecordEntity.startedAt),
            finishedAt = chargeRecordEntity.finishedAt?.let { Timestamp.from(it) },
            initialCapacity = chargeRecordEntity.initialCapacity,
            chargedCapacity = chargeRecordEntity.chargedCapacity,
            dischargedCapacity = chargeRecordEntity.dischargedCapacity,
            charger = charger,
            battery = battery
        )
    }

    fun toChargeTracking(chargeTrackingEntity: ChargeTrackingEntity, cells: List<Cell>): ChargeTrackingID{
        return ChargeTrackingID(
            timestamp = Timestamp.from(chargeTrackingEntity.timestamp.value),
            charge_record_id = chargeTrackingEntity.chargeRecordEntity.id.value,
            charging = chargeTrackingEntity.charging,
            voltage = chargeTrackingEntity.voltage,
            current = chargeTrackingEntity.current,
            capacity = chargeTrackingEntity.capacity,
            real_capacity = chargeTrackingEntity.realCapacity,
            cells = cells
        )
    }

    fun toFormatedChargeTracking(chargeTrackingEntity: ChargeTrackingEntity, cells: List<FormatedCell>): FormatedChargeTracking{
        return FormatedChargeTracking(
            timestamp = Timestamp.from(chargeTrackingEntity.timestamp.value),
            charge_record_id = chargeTrackingEntity.chargeRecordEntity.id.value,
            charging = chargeTrackingEntity.charging,
            real_capacity = convertChargedOrDischargedCapacityToMilliAmpHour(chargeTrackingEntity.realCapacity),
            capacity = convertChargedOrDischargedCapacityToMilliAmpHour(chargeTrackingEntity.capacity),
            voltage = convertVoltageToVolt(chargeTrackingEntity.voltage),
            current = convertCurrentToAmpere(chargeTrackingEntity.current),
            cells = cells
        )
    }

}