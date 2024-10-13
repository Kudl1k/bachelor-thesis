package cz.kudladev.system

import java.nio.ByteBuffer
import java.nio.ByteOrder

object BufferParsers {

    fun conradChargeManager2010(byteArray: ByteArray): ParserResult{
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

    fun turnigyAccucell6(frame: String,cellNumber: Int): ParserResult {
        val state = frame[7].code and 0x7f
        val running = (frame[23].code and 1) != 0
        val charging = (state and 0x01) != 0
        val current = ((frame[32].code and 0x7f) * 1000) + ((frame[33].code and 0x7f) * 10)
        val voltage = ((frame[34].code and 0x7f) * 1000) + ((frame[35].code and 0x7f) * 10)
        val capacity = (((0.1 * (frame[42].code and 0x7f)) + (0.001 * (frame[43].code and 0x7f))) * 100).toInt()

        var cells = emptyList<Pair<Int,Int>>()
        if (cellNumber > 1) {
            cells = (0 until  cellNumber).mapIndexed { index, i ->
                val cellVoltage = ((frame[44 + (i * 2)].code and 0x7f) * 1000) + ((frame[45 + (i * 2)].code and 0x7f) * 10)
                index to cellVoltage
            }
        }


        return ParserResult(
            slot = 1,
            state = if (running) {
                if (charging) State.CHARGING else State.DISCHARGING
            } else {
                State.END
            },
            current = current,
            voltage = voltage,
            capacity = capacity,
            cells = cells
        )
    }
}


data class ParserResult(
    val slot: Int,
    val state: State,
    val current: Int,
    val voltage: Int,
    val capacity: Int,
    val cells: List<Pair<Int,Int>> = emptyList()
)

enum class State {
    NO_BATTERY,
    CHARGING,
    DISCHARGING,
    END
}