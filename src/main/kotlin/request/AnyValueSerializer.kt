package com.criticalay.request

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Custom kotlinx.Serialization [KSerializer] that can encode heterogeneous
 * [Any] values (String, Int, Long, Double, Boolean) into JSON primitives.
 *
 * This is used for the [GaEvent.params] map which accepts arbitrary value types.
 */
object AnyValueSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AnyValue")

    override fun serialize(encoder: Encoder, value: Any) {
        val jsonEncoder = encoder as? JsonEncoder
            ?: error("AnyValueSerializer only supports JSON encoding")
        val element: JsonElement = when (value) {
            is String  -> JsonPrimitive(value)
            is Int     -> JsonPrimitive(value)
            is Long    -> JsonPrimitive(value)
            is Double  -> JsonPrimitive(value)
            is Float   -> JsonPrimitive(value.toDouble())
            is Boolean -> JsonPrimitive(value)
            else       -> JsonPrimitive(value.toString())
        }
        jsonEncoder.encodeJsonElement(element)
    }

    override fun deserialize(decoder: Decoder): Any {
        val jsonDecoder = decoder as? JsonDecoder
            ?: error("AnyValueSerializer only supports JSON decoding")
        return when (val element = jsonDecoder.decodeJsonElement()) {
            is JsonPrimitive -> when {
                element.isString          -> element.content
                element.booleanOrNull != null -> element.boolean
                element.longOrNull != null    -> element.long
                element.doubleOrNull != null  -> element.double
                else                          -> element.content
            }
            else -> element.toString()
        }
    }
}
