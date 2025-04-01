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
    bytes: Int,
    parserId: Int,
    cells: Int,
    targetSlot: Int
): PortData {
    resetPort(port)
    if (parserId == 1) {
        resetPort(port)
        val fullCycle = port.readBytes(34 * 4)
        if (targetSlot != null && targetSlot in 1..4) {
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
        while (true) {
            try {
                val byte = port.readBytes(1) ?: continue
                val c = byte[0].toChar()

                if (c == '{') {
                    frame = ""
                } else if (c == '}' && frame.length == 74) {
                    val result = BufferParsers.turnigyAccucell6(frame,cells)
                    println(result)
                    return result
                } else {
                    frame += c
                }
            } catch (e: Exception) {
                System.err.println(e)
            }
        }
    } else {
        throw IllegalArgumentException("Unknown parser id")
    }
}

fun resetPort(port: SerialPort) {
    port.purgePort(SerialPort.PURGE_RXCLEAR or SerialPort.PURGE_TXCLEAR)
}

fun closePort(port: SerialPort) {
    port.closePort()
}