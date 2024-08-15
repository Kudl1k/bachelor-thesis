package cz.kudladev.backend.utils

import jssc.SerialPortList

fun getPorts(): Array<String> {
    return SerialPortList.getPortNames() ?: emptyArray()
}