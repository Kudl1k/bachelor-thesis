package cz.kudladev.system

import java.nio.ByteBuffer
import java.nio.ByteOrder

object BufferParsers {

    fun conradManagerCharger2010(byteArray: ByteArray): ParserResult{
        val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN)
        val slot = buffer.get(0).toInt() and 0xFF
        buffer.position(13)
        val current = buffer.short.toInt() and 0xFFFF
        buffer.position(15)
        val batteryVoltage = buffer.short.toInt() and 0xFFFF
        buffer.position(17)
        val chargedCapacity = ((buffer.get().toInt() and 0xFF) shl 16) or
                ((buffer.get().toInt() and 0xFF) shl 8) or
                (buffer.get().toInt() and 0xFF)
        buffer.position(20)
        val dischargedCapacity = ((buffer.get().toInt() and 0xFF) shl 16) or
                ((buffer.get().toInt() and 0xFF) shl 8) or
                (buffer.get().toInt() and 0xFF)
        return ParserResult(
            slot = slot,
            current = current,
            voltage = batteryVoltage,
            charged = chargedCapacity,
            discharged = dischargedCapacity
        )
    }

}


data class ParserResult(
    val slot: Int? = null,
    val current: Int? = null,
    val voltage: Int? = null,
    val charged: Int? = null,
    val discharged: Int? = null,
)