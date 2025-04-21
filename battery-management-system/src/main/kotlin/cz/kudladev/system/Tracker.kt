package cz.kudladev.system

import DatabaseBuilder
import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.CellDao
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import jssc.SerialPort
import kotlinx.coroutines.Job
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


var isRunning = false
var job: Job? = null
var openPort: SerialPort? = null

data class SlotState(
    val batteryId: String,
    val slotNumber: Int,
    val maximumChargedCapacity: Int,
    val lastChargedCapacity: Int,
    val lastDischargedCapacity: Int,
    val lastValues : ArrayDeque<Int>,
    val attemptsToAddValue : Int = 0,
    val endAttempts: Int = 0,
    val initialCapacity: Int,
    val realCapacity: Int = 0,
    var charged: Boolean,
    var discharged: Boolean,
    var charging: Boolean? = null,
    val running: Boolean
)

@OptIn(ObsoleteCoroutinesApi::class)
suspend fun startTracking(
    charger: ChargerWithTypesAndSizes,
    batteryWithSlot: List<BatteryWithSlot>,
    chargeTrackingDao: ChargeTrackingDao,
    chargeRecordsDao: ChargeRecordsDao,
    batteriesDao: BatteriesDao,
    cellDao: CellDao
) {
    val slots = charger.slots
    var slotsCounter = 0
    var slotStates = mutableListOf<SlotState>()

    val chargeRecords = mutableListOf<ChargeRecord>()
    var cellNumber = 0

    for (battery in batteryWithSlot) {
        val fetchedBattery = batteriesDao.getBatteryById(battery.id)

        val chargeRecord = ChargeRecordInsert(
            slot = battery.slot,
            battery_id = battery.id,
            charger_id = charger.id!!
        )

        slotStates.add(SlotState(
            batteryId = battery.id,
            slotNumber = battery.slot,
            maximumChargedCapacity = 0,
            lastChargedCapacity = 0,
            lastDischargedCapacity = 0,
            lastValues = ArrayDeque<Int>(),
            endAttempts = 0,
            initialCapacity = 0,
            charged = false,
            discharged = false,
            charging = null,
            running = true
        ))
        val createdChargeRecord = chargeRecordsDao.createChargeRecord(chargeRecord)
        if (fetchedBattery != null) {
            if (fetchedBattery.cells > 1) {
                (1..fetchedBattery.cells).map { index ->
                    cellDao.createCell(
                        CellModel(
                            idChargeRecord = createdChargeRecord.idChargeRecord!!,
                            number = index
                        )
                    )
                }
            }
            cellNumber = fetchedBattery.cells
        }
        chargeRecords.add(createdChargeRecord)
    }
    var slot = 1
    var nullReadCount = 0
    val maxNullReads = 3
    while (isRunning) {
        if (slotsCounter == slots) {
            delay(5000)
            slotsCounter = 0
        } else {
            slotsCounter++
        }
        val data = try {
            val result = readFromPort(
                openPort!!,
                charger.parser.id,
                cellNumber,
                slot
            )
            nullReadCount = 0
            result
        } catch (e: Exception) {
            println("Error reading from port: ${e.message}")
            nullReadCount++
            null
        }

        if (data == null) {
            println("Null data received from port. Attempt $nullReadCount of $maxNullReads")
            if (nullReadCount >= maxNullReads) {
                println("Port appears to be dead after $maxNullReads consecutive failed reads. Ending all charge records.")
                for (slot in slotStates) {
                    if (slot.running) {
                        var charge_record_id = -1
                        for (chargeRecord in chargeRecords) {
                            if (chargeRecord.slot == slot.slotNumber) {
                                charge_record_id = chargeRecord.idChargeRecord!!
                            }
                        }

                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(
                            EndOfCharging(type = "end_of_charging", charge_record_id = charge_record_id)
                        ))
                        chargeRecordsDao.endChargeRecord(charge_record_id, slot.lastChargedCapacity, slot.lastDischargedCapacity)
                        batteriesDao.updateBatteryLastChargingCapacity(
                            slot.batteryId,
                            slot.lastChargedCapacity
                        )
                    }
                }
                isRunning = false
                break
            }
            delay(1000)
            continue
        } else {
            nullReadCount = 0
        }

        slot = (slot % slots) + 1
        println("Data: $data")
        for (slot in slotStates) {
            if (data.slot != slot.slotNumber) continue

            var charge_record_id = -1
            for (chargeRecord in chargeRecords) {
                if (chargeRecord.slot == slot.slotNumber) {
                    charge_record_id = chargeRecord.idChargeRecord!!
                }
            }
            if (data.state != State.NO_BATTERY && data.state != State.END) {
                println(
                    "Slot: ${slot.slotNumber} - Charging: ${slot.charging} - Charged: ${slot.charged} - Discharged: ${slot.discharged} - Current: ${data.current} - Voltage: ${data.voltage} - Capacity: ${data.capacity} - State: ${data.state}"
                )

                if (slot.charging == null){
                    slotStates = slotStates.map {
                        if (it.slotNumber == slot.slotNumber) {
                            if (data.state == State.DISCHARGING) {
                                chargeTrackingDao.createChargeTracking(
                                    chargeTracking = ChargeTrackingID(
                                        charge_record_id = charge_record_id,
                                        charging = false,
                                        real_capacity = 0,
                                        capacity = 0,
                                        voltage = 0,
                                        current = 0,
                                    )
                                )
                            }
                            it.copy(
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
                                it.copy(
                                    charged = true,
                                    charging = false,
                                    lastValues = ArrayDeque<Int>(),
                                    endAttempts = 0,
                                    attemptsToAddValue = 0,
                                    lastDischargedCapacity = 0,
                                )
                            } else {
                                it
                            }
                        }.toMutableList()
                    } else {

                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                println("Charging")
                                val chargeTracking = ChargeTrackingID(
                                    charge_record_id = charge_record_id,
                                    charging = true,
                                    real_capacity = it.realCapacity + data.capacity,
                                    capacity = data.capacity,
                                    voltage = data.voltage,
                                    current = data.current,
                                )
                                val shouldAdd = it.lastValues.checkNewValue(data.capacity - it.lastChargedCapacity)
                                if (shouldAdd || it.attemptsToAddValue >= 5) {
                                    val formated = chargeTrackingDao.createChargeTracking(
                                        chargeTracking = chargeTracking
                                    )
                                    val cells = data.cells.map { (index,cell) ->
                                        cellDao.createCellTracking(
                                            CellTrackingModel(
                                                formated!!.timestamp,
                                                charge_record_id,
                                                index + 1,
                                                cell
                                            )
                                        )
                                    }
                                    DatabaseBuilder.broadcastChannel.send(Json.encodeToString(ChargeTrackingWithCellTrackings(formated!!,cells)))
                                }
                                it.copy(
                                    lastChargedCapacity = data.capacity,
                                    initialCapacity = if (data.capacity > it.initialCapacity) data.capacity else it.initialCapacity,
                                    maximumChargedCapacity = if (data.capacity > it.maximumChargedCapacity) data.capacity else it.maximumChargedCapacity,
                                    lastValues = if (it.attemptsToAddValue >= 5) {
                                        ArrayDeque<Int>()
                                    } else {
                                        it.lastValues
                                    },
                                    attemptsToAddValue = if (!shouldAdd) {
                                        it.attemptsToAddValue + 1
                                    } else {

                                        0
                                    },
                                    endAttempts = 0,
                                    charging = true,
                                    running = true,
                                )
                            } else {
                                it
                            }
                        }.toMutableList()

                    }
                }
                else {
                    if (data.state == State.CHARGING) {
                        println("Switching to charging")
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                it.copy(
                                    charged = true,
                                    charging = true,
                                    running = true,
                                    lastValues = ArrayDeque<Int>(),
                                    endAttempts = 0,
                                    attemptsToAddValue = 0,
                                    lastChargedCapacity = 0
                                )
                            } else {
                                it
                            }
                        }.toMutableList()
                    } else {
                        println("Discharging")
                        var chargeTracking = ChargeTrackingID(
                            charge_record_id = charge_record_id,
                            charging = false,
                            real_capacity = 0,
                            capacity = data.capacity,
                            voltage = data.voltage,
                            current = data.current,
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber) {
                                if (it.initialCapacity < data.capacity && !it.charged) {
                                    println("Discharging before charging")
                                    val shouldAdd = it.lastValues.checkNewValue(data.capacity - it.lastDischargedCapacity)
                                    if (shouldAdd || it.attemptsToAddValue >= 5) {
                                        val inserted = chargeTrackingDao.createChargeTracking(chargeTracking)

                                        val cells = data.cells.map { (index,cell) ->
                                            cellDao.createCellTracking(
                                                CellTrackingModel(
                                                    inserted!!.timestamp,
                                                    charge_record_id,
                                                    index + 1,
                                                    cell
                                                )
                                            )
                                        }
                                        val updated = chargeTrackingDao.updateDischargeTrackingValues(
                                            id_charge_record = charge_record_id,
                                            capacity = data.capacity
                                        )
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(ChargeTrackingsWithCellTrackings(updated,cells)))
                                    }
                                    it.copy(
                                        lastDischargedCapacity = data.capacity,
                                        initialCapacity = data.capacity,
                                        charging = false,
                                        running = true,
                                        endAttempts = 0,
                                        lastValues = if (it.attemptsToAddValue >= 5) {
                                            ArrayDeque<Int>()
                                        } else {
                                            it.lastValues
                                        },
                                        attemptsToAddValue = if (!shouldAdd) {
                                            it.attemptsToAddValue + 1
                                        } else {
                                            0
                                        },
                                        realCapacity = 0
                                    )
                                } else if(it.lastDischargedCapacity < data.capacity && it.charged) {
                                    println("Discharging after charging")
                                    val shouldAdd = it.lastValues.checkNewValue(data.capacity - it.lastDischargedCapacity)
                                    if (shouldAdd || it.attemptsToAddValue >= 5) {
                                        chargeTracking = chargeTracking.copy(
                                            real_capacity = if (data.capacity < it.maximumChargedCapacity) it.lastChargedCapacity - data.capacity else 0
                                        )
                                        val inserted = chargeTrackingDao.createChargeTracking(chargeTracking)

                                        val cells = data.cells.map { (index,cell) ->
                                            cellDao.createCellTracking(
                                                CellTrackingModel(
                                                    inserted!!.timestamp,
                                                    charge_record_id,
                                                    index + 1,
                                                    cell
                                                )
                                            )
                                        }

                                        if (data.capacity > it.maximumChargedCapacity){
                                            chargeTrackingDao.updateChargeTrackingValues(
                                                id_charge_record = charge_record_id,
                                                capacity = data.capacity - it.maximumChargedCapacity,
                                            )
                                            chargeTrackingDao.updateDischargeTrackingValues(
                                                id_charge_record = charge_record_id,
                                                capacity = data.capacity
                                            )
                                            val updated = chargeTrackingDao.getChargeTrackingById(charge_record_id)
                                            DatabaseBuilder.broadcastChannel.send(Json.encodeToString(ChargeTrackingsWithCellTrackings(updated!!,cells)))
                                        } else {
                                            DatabaseBuilder.broadcastChannel.send(Json.encodeToString(ChargeTrackingWithCellTrackings(inserted!!,cells)))
                                        }
                                    }
                                    it.copy(
                                        lastDischargedCapacity = data.capacity,
                                        initialCapacity = data.capacity - it.lastChargedCapacity,
                                        charging = false,
                                        running = true,
                                        endAttempts = 0,
                                        lastValues = if (it.attemptsToAddValue >= 5) {
                                            ArrayDeque<Int>()
                                        } else {
                                            it.lastValues
                                        },
                                        attemptsToAddValue = if (!shouldAdd) {
                                            it.attemptsToAddValue + 1
                                        } else {

                                            0
                                        },
                                        realCapacity = chargeTracking.real_capacity
                                    )
                                } else {
                                    println("Constant discharging")
                                    val shouldAdd = it.lastValues.checkNewValue(data.capacity - it.lastDischargedCapacity)
                                    if (shouldAdd || it.attemptsToAddValue >= 5) {
                                        val inserted = chargeTrackingDao.createChargeTracking(
                                            chargeTracking.copy(
                                                real_capacity = if (data.capacity < it.maximumChargedCapacity) it.lastChargedCapacity - data.capacity else 0
                                            )
                                        )
                                        val cells = data.cells.map { (index,cell) ->
                                            cellDao.createCellTracking(
                                                CellTrackingModel(
                                                    inserted!!.timestamp,
                                                    charge_record_id,
                                                    index + 1,
                                                    cell
                                                )
                                            )
                                        }
                                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(ChargeTrackingWithCellTrackings(inserted!!,cells)))
                                    }
                                    it.copy(
                                        lastDischargedCapacity = data.capacity,
                                        charging = false,
                                        running = true,
                                        endAttempts = 0,
                                        lastValues = if (it.attemptsToAddValue >= 5) {
                                            ArrayDeque<Int>()
                                        } else {
                                            it.lastValues
                                        },
                                        attemptsToAddValue = if (!shouldAdd) {
                                            it.attemptsToAddValue + 1
                                        } else {
                                            0
                                        },
                                        realCapacity = chargeTracking.real_capacity
                                    )
                                }
                            } else {
                                it
                            }
                        }.toMutableList()
                    }
                }
            }
            else if (slot.endAttempts == 3 && slot.running) {
                println("Ending charge record")
                slotStates = slotStates.map {
                    if (it.slotNumber == slot.slotNumber) {
                        DatabaseBuilder.broadcastChannel.send(Json.encodeToString(EndOfCharging(type = "end_of_charging", charge_record_id = charge_record_id)))
                        chargeRecordsDao.endChargeRecord(charge_record_id, it.lastChargedCapacity,it.lastDischargedCapacity)
                        batteriesDao.updateBatteryLastChargingCapacity(
                            slot.batteryId,
                            it.lastChargedCapacity
                        )
                        it.copy(
                            running = false
                        )
                    } else {
                        it
                    }
                }.toMutableList()
            }
            else if ((data.state == State.NO_BATTERY || data.state == State.END) && slot.running) {
                slotStates = slotStates.map {
                    if (it.slotNumber == slot.slotNumber) {
                        val endAttempts = it.endAttempts + 1
                        it.copy(
                            endAttempts = endAttempts,
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

private fun ArrayDeque<Int>.checkNewValue(
    newValue: Int
): Boolean{
    if (newValue == 0){
        println("Returning true, but not adding new value : $newValue -  ${this.joinToString(",")}")
        return true
    }
    if (this.size < 10) {
        println("Queue is too small, adding new value : $newValue -  ${this.joinToString(",")}")
        this.add(newValue)
        return true
    }
    val average = this.average()
    if (newValue > average * 5) {
        println("New value is too high, not adding : $newValue - Average: $average - ${this.joinToString(",")}")
        return false
    } else {
        println("New value is acceptable, adding : $newValue - Average: $average - ${this.joinToString(",")}")
        this.removeFirst()
        this.add(newValue)
        return true
    }
}


fun stopTracking() {
    isRunning = false
    openPort?.closePort()
    job?.cancel()
    job = null
}