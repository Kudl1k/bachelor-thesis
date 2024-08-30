package cz.kudladev.system

import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.BatteriesDao
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import jssc.SerialPort
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay


var isRunning = false
var job: Job? = null
var openPort: SerialPort? = null

data class SlotState(
    val battery_id: Int,
    val slotNumber: Int,
    var last_capacity: Int = 0,
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

    val chargeRecords = mutableListOf<ChargeRecord>()

    for (battery in batteryWithSlot){
        val chargeRecord = ChargeRecordInsert(
            program = "C",
            slot = battery.slot,
            battery_id = battery.id,
            charger_id = charger.id!!
        )
        slotStates.add(SlotState(battery.id,battery.slot, 0,true))
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
        when (chargeRecords[0].program) {
            "C" -> {
                for (slot in slotStates){
                    var chargerId = -1
                    for (chargeRecord in chargeRecords){
                        if (chargeRecord.slot == slot.slotNumber){
                            chargerId = chargeRecord.idChargeRecord!!
                        }
                    }
                    if (data?.current!! > 0 && data.slot == slot.slotNumber){
                        println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Charged: ${data?.charged}")

                        val chargeTracking = ChargeTrackingID(
                            charge_record_id = chargerId,
                            capacity = data.charged!!,
                            voltage = data.voltage!!,
                            current = data.current
                        )
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber){
                                SlotState(
                                    it.battery_id,
                                    it.slotNumber,
                                    data.charged,
                                    true
                                )
                            } else {
                                it
                            }
                        }.toMutableList()
                        chargeTrackingDao.createChargeTracking(
                            chargeTracking = chargeTracking
                        )
                    } else if(data.current == 0 && data.slot == slot.slotNumber){
                        slotStates = slotStates.map {
                            if (it.slotNumber == slot.slotNumber){
                                chargeRecordsDao.endChargeRecord(chargerId, it.last_capacity)
                                batteriesDao.updateBatteryLastChargingCapacity(
                                    slot.battery_id,
                                    it.last_capacity
                                )
                                SlotState(
                                    it.battery_id,
                                    it.slotNumber,
                                    it.last_capacity,
                                    false
                                )
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