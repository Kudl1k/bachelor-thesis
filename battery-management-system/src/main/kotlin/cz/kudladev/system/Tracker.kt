package cz.kudladev.system

import DatabaseBuilder
import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import jssc.SerialPort
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


var isRunning = false
var job: Job? = null
var openPort: SerialPort? = null

data class SlotState(
    val battery_id: String,
    val slotNumber: Int,
    val last_charged_capacity: Int,
    val last_discharged_capacity: Int,
    val initial_capacity: Int,
    var charged: Boolean,
    var discharged: Boolean,
    var charging: Boolean? = null,
    val running: Boolean
)

suspend fun startTracking(
    charger: ChargerWithTypesAndSizes,
    batteryWithSlot: List<BatteryWithSlot>,
    chargeTrackingDao: ChargeTrackingDao,
    chargeRecordsDao: ChargeRecordsDao,
    batteriesDao: BatteriesDao
) {
    val slots = charger.slots
    var slotsCounter = 0
    var slotStates = mutableListOf<SlotState>()

    val chargeRecords = mutableListOf<ChargeRecord>()

    for (battery in batteryWithSlot) {
        val chargeRecord = ChargeRecordInsert(
            slot = battery.slot,
            battery_id = battery.id,
            charger_id = charger.id!!
        )

        slotStates.add(SlotState(
            battery_id = battery.id,
            slotNumber = battery.slot,
            last_charged_capacity = 0,
            last_discharged_capacity = 0,
            initial_capacity = 0,
            charged = false,
            discharged = false,
            charging = null,
            running = true
        ))
        val createdChargeRecord = chargeRecordsDao.createChargeRecord(chargeRecord)
        chargeRecords.add(createdChargeRecord)
        chargeTrackingDao.createChargeTracking(
            chargeTracking = ChargeTrackingID(
                charge_record_id = createdChargeRecord.idChargeRecord!!,
                charging = false,
                real_capacity = 0,
                capacity = 0,
                voltage = 0,
                current = 0,
                cells = emptyList()
            )
        )
    }

    while (isRunning) {
        if (slotsCounter == slots) {
            delay(5000)
            slotsCounter = 0
        } else {
            slotsCounter++
        }
        val data = readFromPort(
            openPort!!,
            34,
            charger.id!!
        )
        for (slot in slotStates) {
            if (data.slot != slot.slotNumber) continue

            var charge_record_id = -1
            for (chargeRecord in chargeRecords) {
                if (chargeRecord.slot == slot.slotNumber) {
                    charge_record_id = chargeRecord.idChargeRecord!!
                }
            }
            if (data.state != State.NO_BATTERY) {
                println(
                    "Slot: ${slot.slotNumber} - Charging: ${slot.charging} - Charged: ${slot.charged} - Discharged: ${slot.discharged} - Current: ${data.current} - Voltage: ${data.voltage} - Capacity: ${data.capacity} - State: ${data.state}"
                )

                if (slot.charging == null){
                    slotStates = slotStates.map {
                        if (it.slotNumber == slot.slotNumber) {
                            SlotState(
                                battery_id = it.battery_id,
                                slotNumber = it.slotNumber,
                                last_charged_capacity = it.last_charged_capacity,
                                last_discharged_capacity = it.last_discharged_capacity,
                                initial_capacity = it.initial_capacity,
                                charged = it.charged,
                                discharged = it.discharged,
                                charging = data.state == State.CHARGING,
                                running = true
                            )
                        } else {
                            it
                        }
                    }.toMutableList()
                }

                if (slot.charging == true) {
                    if (data.state == State.DISCHARGING) {
                        println("Switching to discharging")
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = it.last_charged_capacity,
                                    last_discharged_capacity = it.last_discharged_capacity,
                                    initial_capacity = it.initial_capacity,
                                    charged = true,
                                    discharged = it.discharged,
                                    charging = false,
                                    running = true
                                )
                            } else {
                                it
                            }
                        }.toMutableList()
                    } else {
                        println("Charging")
                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = charge_record_id,
                            charging = true,
                            real_capacity = data.capacity,
                            capacity = data.capacity,
                            voltage = data.voltage,
                            current = data.current,
                            cells = emptyList()
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                val slotState = SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = data.capacity,
                                    last_discharged_capacity = it.last_discharged_capacity,
                                    initial_capacity = slot.initial_capacity,
                                    charged = it.charged,
                                    discharged = it.discharged,
                                    charging = true,
                                    running = true
                                )
                                slotState
                            } else {
                                it
                            }
                        }.toMutableList()
                        val formated = chargeTrackingDao.createChargeTracking(
                            chargeTracking = chargeTracking
                        )
                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(formated))
                    }
                } else {
                    if (data.state == State.CHARGING) {
                        println("Switching to charging")
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = it.last_charged_capacity,
                                    last_discharged_capacity = it.last_discharged_capacity,
                                    initial_capacity = slot.initial_capacity,
                                    charged = it.charged,
                                    discharged = true,
                                    charging = true,
                                    running = true
                                )
                            } else {
                                it
                            }
                        }.toMutableList()
                    } else {
                        println("Discharging")
                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = charge_record_id,
                            charging = false,
                            real_capacity = 0,
                            capacity = data.capacity,
                            voltage = data.voltage,
                            current = data.current,
                            cells = emptyList()
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                val state: SlotState =
                                    if (it.initial_capacity < data.capacity && !it.charged) {
                                        println("Discharging before charging")
                                        chargeTrackingDao.createChargeTracking(chargeTracking)
                                        val updated = chargeTrackingDao.updateDischargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.capacity
                                        )
                                        println(updated)
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(updated))
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = it.last_charged_capacity,
                                            last_discharged_capacity = data.capacity,
                                            initial_capacity = data.capacity,
                                            charged = it.charged,
                                            discharged = it.discharged,
                                            charging = false,
                                            running = true
                                        )
                                    } else if(it.last_charged_capacity < data.capacity && it.charged) {
                                        println("Discharging after charging")
                                        chargeTrackingDao.createChargeTracking(chargeTracking)
                                        chargeTrackingDao.updateChargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.capacity - it.last_charged_capacity
                                        )
                                        chargeTrackingDao.updateDischargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.capacity
                                        )
                                        val updated = chargeTrackingDao.getChargeTrackingById(charge_record_id)
                                        println(updated)
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(updated))
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = it.last_charged_capacity,
                                            last_discharged_capacity = data.capacity,
                                            initial_capacity = data.capacity - it.last_charged_capacity,
                                            charged = it.charged,
                                            discharged = it.discharged,
                                            charging = false,
                                            running = true
                                        )
                                    } else {
                                        println("Constant discharging")
                                        val inserted = chargeTrackingDao.createChargeTracking(
                                            chargeTracking.copy(
                                                real_capacity = if (!it.charged) 0 else it.last_discharged_capacity - data.capacity
                                            )
                                        )
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(inserted))
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = it.last_charged_capacity,
                                            last_discharged_capacity = data.capacity,
                                            initial_capacity = slot.initial_capacity,
                                            charged = it.charged,
                                            discharged = it.discharged,
                                            charging = false,
                                            running = true
                                        )
                                    }
                                state
                            } else {
                                it
                            }
                        }.toMutableList()
                    }
                }
            } else if ((data.state == State.END || data.state == State.NO_BATTERY) && data.slot == slot.slotNumber) {
                println("Ending charge record")
                slotStates = slotStates.map {
                    if (it.slotNumber == slot.slotNumber) {
                        chargeRecordsDao.endChargeRecord(charge_record_id, it.last_charged_capacity,it.last_discharged_capacity)
                        batteriesDao.updateBatteryLastChargingCapacity(
                            slot.battery_id,
                            it.last_charged_capacity
                        )
                        SlotState(
                            battery_id = it.battery_id,
                            slotNumber = it.slotNumber,
                            initial_capacity = it.initial_capacity,
                            last_charged_capacity = it.last_charged_capacity,
                            last_discharged_capacity = it.last_discharged_capacity,
                            charged = it.charged,
                            discharged = it.discharged,
                            charging = it.charging,
                            running = false
                        )
                    } else {
                        it
                    }
                }.toMutableList()
            }
        }
        if (slotStates.all { !it.running }) {
            isRunning = false
        }
    }
    stopTracking()
}



fun stopTracking() {
    isRunning = false
    openPort?.closePort()
    job?.cancel()
    job = null
}