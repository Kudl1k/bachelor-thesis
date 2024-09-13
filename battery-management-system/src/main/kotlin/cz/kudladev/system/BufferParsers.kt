package cz.kudladev.system

import java.nio.ByteBuffer
import java.nio.ByteOrder

object BufferParsers {

    fun conradManagerCharger2010(byteArray: ByteArray): ParserResult{
        val buffer = ByteBuffer.wrap(byteArray).order(ByteOrder.BIG_ENDIAN)
        val slot = buffer.get(0).toInt() and 0xFF
        val currentLo = buffer.get(2).toInt() and 0xFF
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

        val state = when(currentLo) {
            0 -> State.NO_BATTERY
            1,3,5,7 -> State.CHARGING
            2,4,6 -> State.DISCHARGING
            else -> State.END
        }
        val capacity = if (state == State.CHARGING) chargedCapacity else dischargedCapacity
        return ParserResult(slot, state, current, batteryVoltage, capacity)
    }

}


data class ParserResult(
    val slot: Int,
    val state: State,
    val current: Int,
    val voltage: Int,
    val capacity: Int,
)

enum class State {
    NO_BATTERY,
    CHARGING,
    DISCHARGING,
    END
}