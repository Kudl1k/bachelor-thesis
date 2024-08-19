package cz.kudladev.system

import cz.kudladev.data.models.Charger
import kotlinx.coroutines.Job


var isRunning = false
var job: Job? = null

suspend fun run() {
    while (isRunning) {
        println("Hello, World!")
        Thread.sleep(1000)
    }
}

suspend fun startTracking(
    charger: Charger,
    program: Char
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
    while (isRunning) {
        val data = readFromPort(
            port = openPort,
            bytes = 24,
            idCharger = 1,
        )
        when (program) {
            'C' -> {
                println("Charging program")
                println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Charged: ${data?.charged}")
            }
            'D' -> {
                println("Discharging program")
                println("Slot: ${data?.slot}; Current: ${data?.current}; Voltage: ${data?.voltage}; Discharged: ${data?.discharged}")
            }
        }
        Thread.sleep(3000)
    }
}

suspend fun stopTracking() {
    isRunning = false
    job?.cancel()
    job = null
}