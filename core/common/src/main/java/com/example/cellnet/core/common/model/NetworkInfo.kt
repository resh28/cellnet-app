package com.example.cellnet.core.common.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.encodeToString

@Serializable
data class NetworkInfo(
    val uid: String = "",
    val userId: String = "",
    val cellTowerId: String = "",
    val deviceId: String = "",
    val networkOperator: String = "",
    val networkClass: String = "",
    val phoneType: String = "",
    val downloadSpeed: Int = 0,
    val uploadSpeed: Int = 0,
    val signalStrength: Int? = null,
    @Contextual
    @Serializable(with = LocalDateTimeSerializer::class)
    val timeStamp: LocalDateTime? = null,
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
)

@RequiresApi(Build.VERSION_CODES.O)
object LocalDateTimeSerializer : KSerializer<LocalDateTime> {

    private val formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME

    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        return LocalDateTime.parse(decoder.decodeString(), formatter)
    }
}
