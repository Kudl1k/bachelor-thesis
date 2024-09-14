package cz.kudladev.system

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
    println(port.isOpened)
    return port
}

fun readFromPort(
    port: SerialPort,
    bytes: Int,
    idCharger: Int,
    cells: Int,
): ParserResult {
    resetPort(port)
    if (idCharger == 1) {
        println("Conrad Charge Manager 2010")
        val buffer = port.readBytes(bytes)
        return BufferParsers.conradChargeManager2010(buffer)
    } else {
        println("Turnigy Accucell 6")
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
    }


}

fun resetPort(port: SerialPort) {
    port.purgePort(SerialPort.PURGE_RXCLEAR or SerialPort.PURGE_TXCLEAR)
}

fun closePort(port: SerialPort) {
    port.closePort()
}