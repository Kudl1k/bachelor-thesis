package cz.kudladev.system

import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import jssc.SerialPort
import kotlinx.coroutines.Job


var isRunning = false
var job: Job? = null
var openPort: SerialPort? = null

data class SlotState(
    val slot: Int,
    val running: Boolean
)

suspend fun startTracking(
    charger: ChargerWithTypesAndSizes,
    batteryWithSlot: List<BatteryWithSlot>,
    chargeTrackingDao: ChargeTrackingDao,
    chargeRecordsDao: ChargeRecordsDao,
    batteriesDao: BatteriesDao
){
    if (!openPort?.isOpened!!) {

    }
    val slots = charger.slots
    var slotsCounter = 0
    var slotStates = mutableListOf<SlotState>()
    var last_capacity = 0

    val chargeRecords = mutableListOf<ChargeRecord>()

    for (battery in batteryWithSlot){
        val chargeRecord = ChargeRecordInsert(
            program = "C",
            slot = battery.slot,
            battery_id = battery.id!!,
            charger_id = charger.id!!
        )
        slotStates.add(SlotState(battery.slot, true))
        chargeRecords.add(chargeRecordsDao.createChargeRecord(chargeRecord))
    }

    while (isRunning) {
        if (slotsCounter == slots) {
            Thread.sleep(5000)
            slotsCounter = 0
        } else {
            slotsCounter++
        }
        val data = readFromPort(
            openPort!!,
            34,
            charger.id!!
        )
        when (chargeRecords[0].program) {
            "C" -> {
                for (battery in batteryWithSlot){
                    var chargerId = -1
                    for (chargeRecord in chargeRecords){
                        if (chargeRecord.slot == battery.slot){
                            chargerId = chargeRecord.idChargeRecord!!
                        }
                    }
                    if (data?.current!! > 0 && data.slot == battery.slot){
                        println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Charged: ${data?.charged}")

                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = chargerId,
                            capacity = data.charged!!,
                            voltage = data.voltage!!,
                            current = data.current!!
                        )
                        last_capacity = data.charged!!
                        chargeTrackingDao.createChargeTracking(
                            chargeTracking = chargeTracking
                        )
                    } else if(data.current == 0 && data.slot == battery.slot){
                        slotStates = slotStates.map {
                            if (it.slot == battery.slot){
                                chargeRecordsDao.endChargeRecord(chargerId, last_capacity)
                                batteriesDao.updateBatteryLastChargingCapacity(
                                    battery.id,
                                    last_capacity
                                )
                                SlotState(it.slot, false)
                            } else {
                                it
                            }
                        }.toMutableList()
                    }
                }
                if (slotStates.all { !it.running }){
                    isRunning = false
                }
            }
            "D" -> {
                println("Discharging program")
                println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Discharged: ${data?.discharged}")
            }
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