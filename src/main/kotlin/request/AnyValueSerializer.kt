/*
 * Copyright 2026 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
