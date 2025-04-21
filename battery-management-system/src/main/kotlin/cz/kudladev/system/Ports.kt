package cz.kudladev.system

import cz.kudladev.system.BufferParsers.conradChargeManager2010
import jssc.SerialPort
import jssc.SerialPortList


fun getAvailablePorts(): List<String> {
    return SerialPortList.getPortNames().toList()
}

fun openPort(
    portName: String,
    baudRate: Int,
    dataBits: Int,
    stopBits: Int,
    parity: Int,
    rts: Boolean,
    dtr: Boolean
): SerialPort {
    val port = SerialPort(portName)
    port.openPort()
    port.setParams(
        baudRate,
        dataBits,
        stopBits,
        parity,
        rts,
        dtr
    )
    resetPort(port)
    println(port.isOpened)
    return port
}

fun readFromPort(
    port: SerialPort,
    parserId: Int,
    cells: Int,
    targetSlot: Int
): PortData? {
    try {
        resetPort(port)
        if (parserId == 1) {
            resetPort(port)
            val fullCycle = try {
                port.readBytes(34 * 4)
            } catch (e: Exception) {
                println("Error reading from port: ${e.message}")
                return null
            }

            if (fullCycle == null || fullCycle.isEmpty()) {
                println("Received empty data from port")
                return null
            }

            if (targetSlot in 1..4) {
                for (i in 0 until 4) {
                    val startIndex = i * 34
                    if (startIndex + 34 <= fullCycle.size) {
                        val slotNum = fullCycle[startIndex].toInt() and 0xFF
                        if (slotNum == targetSlot) {
                            val slotData = ByteArray(34)
                            System.arraycopy(fullCycle, startIndex, slotData, 0, 34)
                            println("Raw bytes for slot $slotNum: ${slotData.joinToString(", ") { it.toUByte().toString() }}")
                            return conradChargeManager2010(slotData)
                        }
                    }
                }
                println("Warning: Couldn't find data for slot $targetSlot in the current cycle")
            }
            println("Warning: Couldn't find valid slot data in the current cycle")
            return PortData(0, State.NO_BATTERY, 0, 0, 0)
        } else if (parserId == 2) {
            var frame = ""
            var attempts = 0
            val maxAttempts = 100 // Limit reading attempts to avoid infinite loop

            while (attempts < maxAttempts) {
                attempts++
                try {
                    val byte = port.readBytes(1) ?: continue
                    val c = byte[0].toChar()

                    if (c == '{') {
                        frame = ""
                    } else if (c == '}' && frame.length == 74) {
                        val result = BufferParsers.turnigyAccucell6(frame, cells)
                        println(result)
                        return result
                    } else {
                        frame += c
                    }
                } catch (e: Exception) {
                    println("Error reading from port: ${e.message}")
                    return null
                }
            }
            println("Exceeded maximum read attempts")
            return null
        } else {
            throw IllegalArgumentException("Unknown parser id")
        }
    } catch (e: Exception) {
        println("Error in readFromPort: ${e.message}")
        return null
    }
}

fun resetPort(port: SerialPort) {
    port.purgePort(SerialPort.PURGE_RXCLEAR or SerialPort.PURGE_TXCLEAR)
}

fun closePort(port: SerialPort) {
    port.closePort()
}