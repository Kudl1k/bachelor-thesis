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
    val battery_id: Int,
    val slotNumber: Int,
    val last_charged_capacity: Int,
    val last_discharged_capacity: Int,
    val initial_capacity: Int,
    var charged: Boolean,
    var discharged: Boolean,
    var charging: Boolean,
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
            last_charged_capacity = -1,
            last_discharged_capacity = -1,
            initial_capacity = 0,
            charged = false,
            discharged = false,
            charging = false,
            running = true
        ))
        chargeRecords.add(chargeRecordsDao.createChargeRecord(chargeRecord))
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
            if (data.current!! > 0) {
                println(
                    "Slot: ${slot.slotNumber} - Charging: ${slot.charging} - Charged: ${slot.charged} - Discharged: ${slot.discharged} - Current: ${data.current} - Voltage: ${data.voltage} - Capacity: ${data.charged} - Discharged: ${data.discharged}"
                )

                if (slot.last_charged_capacity == -1 && slot.last_discharged_capacity == -1) {
                    slotStates = slotStates.map {
                        if (it.slotNumber == slot.slotNumber) {
                            SlotState(
                                battery_id = it.battery_id,
                                slotNumber = it.slotNumber,
                                last_charged_capacity = if (data.charged!! > 0) data.charged else it.last_charged_capacity,
                                last_discharged_capacity = if (data.discharged!! > 0) data.discharged else it.last_discharged_capacity,
                                initial_capacity = 0,
                                charged = if (data.charged > 0) true else it.charging,
                                discharged = if (data.discharged > 0) true else it.discharged,
                                charging = if (data.charged > 0) true else it.charging,
                                running = true
                            )
                        } else {
                            it
                        }
                    }.toMutableList()
                    println("Checking mode for slot ${slot.slotNumber} - Charged: ${slot.charged} - Discharged: ${slot.discharged} - Data Discharged: ${data.discharged} - Data Charged: ${data.charged}")
                    continue
                }
                if (slot.charging) {
                    if (data.discharged != slot.last_discharged_capacity) {
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = data.charged!!,
                                    last_discharged_capacity = slot.last_discharged_capacity,
                                    initial_capacity = slot.initial_capacity,
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
                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = charge_record_id,
                            charging = true,
                            real_capacity = data.charged!!,
                            capacity = data.charged,
                            voltage = data.voltage!!,
                            current = data.current
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                val slotState = SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = data.charged,
                                    last_discharged_capacity = data.discharged!!,
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
                        chargeTrackingDao.createChargeTracking(
                            chargeTracking = chargeTracking
                        )
                    }
                } else {
                    if (data.charged != slot.last_charged_capacity) {
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                SlotState(
                                    battery_id = it.battery_id,
                                    slotNumber = it.slotNumber,
                                    last_charged_capacity = data.charged!!,
                                    last_discharged_capacity = data.discharged!!,
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

                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = charge_record_id,
                            charging = false,
                            real_capacity = 0,
                            capacity = data.discharged!!,
                            voltage = data.voltage!!,
                            current = data.current
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                val state: SlotState =
                                    if (it.initial_capacity < data.discharged && !it.charged) {
                                        println("Discharging before charging")
                                        chargeTrackingDao.createChargeTracking(chargeTracking)
                                        val updated = chargeTrackingDao.updateDischargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.discharged
                                        )
                                        println(updated)
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(updated))
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = data.charged!!,
                                            last_discharged_capacity = data.discharged,
                                            initial_capacity = data.discharged,
                                            charged = it.charged,
                                            discharged = it.discharged,
                                            charging = false,
                                            running = true
                                        )
                                    } else if(it.last_charged_capacity < data.discharged && it.charged) {
                                        println("Discharging after charging")
                                        chargeTrackingDao.createChargeTracking(chargeTracking)
                                        chargeTrackingDao.updateChargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.discharged - it.last_charged_capacity
                                        )
                                        chargeTrackingDao.updateDischargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.discharged
                                        )
                                        val updated = chargeTrackingDao.getChargeTrackingById(charge_record_id)
                                        println(updated)
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(updated))
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = data.charged!!,
                                            last_discharged_capacity = data.discharged,
                                            initial_capacity = data.discharged - it.last_charged_capacity,
                                            charged = it.charged,
                                            discharged = it.discharged,
                                            charging = false,
                                            running = true
                                        )
                                    } else {
                                        println("Constant discharging")
                                        chargeTrackingDao.createChargeTracking(
                                            chargeTracking.copy(
                                                real_capacity = if (!it.charged) 0 else it.last_discharged_capacity - data.discharged
                                            )
                                        )
                                        SlotState(
                                            battery_id = it.battery_id,
                                            slotNumber = it.slotNumber,
                                            last_charged_capacity = data.charged!!,
                                            last_discharged_capacity = data.discharged,
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
            } else if (data.current == 0 && data.slot == slot.slotNumber) {
                slotStates = slotStates.map {
                    if (it.slotNumber == slot.slotNumber) {
                        chargeRecordsDao.endChargeRecord(charge_record_id, it.last_charged_capacity)
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