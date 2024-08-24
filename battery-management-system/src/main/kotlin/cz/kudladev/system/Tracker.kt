package cz.kudladev.system

import cz.kudladev.data.models.*
import cz.kudladev.domain.repository.ChargeRecordsDao
import cz.kudladev.domain.repository.ChargeTrackingDao
import kotlinx.coroutines.Job


var isRunning = false
var job: Job? = null

suspend fun startTracking(
    charger: ChargerWithTypesAndSizes,
    batteryWithSlot: List<BatteryWithSlot>,
    chargeTrackingDao: ChargeTrackingDao,
    chargeRecordsDao: ChargeRecordsDao
){
    val openPort = openPort(
        portName = charger.tty,
        baudRate = charger.baudRate,
        dataBits = charger.dataBits,
        stopBits = charger.stopBits,
        parity = charger.parity,
        rts = charger.rts,
        dtr = charger.dtr
    )
    val slots = charger.slots
    var counter = 0

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
        val data = readFromPort(
            port = openPort,
            bytes = 34,
            idCharger = charger.id!!,
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
                        chargeTrackingDao.createChargeTracking(
                            chargeTracking = chargeTracking
                        )
                    } else if(data.current == 0 && data.slot == battery.slot){
                        println("There is no current in the slot. Ending program.")
                        chargeRecordsDao.endChargeRecord(chargerId, data.charged!!)
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

suspend fun stopTracking() {
    isRunning = false
    job?.cancel()
    job = null
}