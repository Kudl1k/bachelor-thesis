package cz.kudladev.system

import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import jssc.SerialPort
import kotlinx.coroutines.Job


var isRunning = false
var job: Job? = null
var openPort: SerialPort? = null

suspend fun startTracking(
    charger: ChargerWithTypesAndSizes,
    batteryWithSlot: List<BatteryWithSlot>,
    chargeTrackingDao: ChargeTrackingDao,
    chargeRecordsDao: ChargeRecordsDao
){
    val slots = charger.slots
    var counter = 0
    var last_capacity = 0

    val chargeRecords = mutableListOf<ChargeRecord>()

    for (battery in batteryWithSlot){
        val chargeRecord = ChargeRecordInsert(
            program = "C",
            slot = battery.slot,
            battery_id = battery.id!!,
            charger_id = charger.id!!
        )
        println(chargeRecord)
        chargeRecords.add(chargeRecordsDao.createChargeRecord(chargeRecord))
    }

    while (isRunning) {
        if (counter == slots) {
            Thread.sleep(5000)
            counter = 0
        } else {
            counter++
        }
        val data = openPort?.let {
            readFromPort(
                port = it,
                bytes = 34,
                idCharger = charger.id!!,
            )
        }
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
                        println("There is no current in the slot. Ending program.")
                        chargeRecordsDao.endChargeRecord(chargerId, last_capacity)
                    }
                }
            }
            "D" -> {
                println("Discharging program")
                println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Discharged: ${data?.discharged}")
            }
        }

    }
}

fun stopTracking() {
    isRunning = false
    openPort?.closePort()
    job?.cancel()
    job = null
}