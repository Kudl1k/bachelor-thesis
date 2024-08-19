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
    return port
}

fun readFromPort(
    port: SerialPort,
    bytes: Int,
    idCharger: Int,
): ParserResult? {
    resetPort(port)
    val buffer = port.readBytes(bytes)

    when (idCharger){
        1 -> {
            return BufferParsers.conradManagerCharger2010(buffer)
        }
    }
    return null
}

fun resetPort(port: SerialPort) {
    port.purgePort(SerialPort.PURGE_RXCLEAR or SerialPort.PURGE_TXCLEAR)
}

fun closePort(port: SerialPort) {
    port.closePort()
}