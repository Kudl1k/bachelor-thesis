package cz.kudladev.backend.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.*

object TimestampSerializer: KSerializer<Timestamp> {
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Timestamp", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Timestamp {
        val string = decoder.decodeString()
        val date = dateFormat.parse(string)
        return Timestamp(date.time)
    }

    override fun serialize(encoder: Encoder, value: Timestamp) {
        val string = dateFormat.format(value)
        encoder.encodeString(string)
    }


}