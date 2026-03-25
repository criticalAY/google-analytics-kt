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

import kotlinx.serialization.Serializable

/**
 * Represents a single GA4 event within a [GaRequest].
 *
 * GA4 Measurement Protocol allows up to 25 events per request, each with
 * an arbitrary set of string/number parameters.
 *
 * @param name   Event name — max 40 chars, letters/digits/underscores only.
 * @param params Arbitrary key-value parameters for this event.
 *               Values may be [String], [Long], [Int], [Double], or [Boolean].
 */
@Serializable
data class GaEvent(
    val name: String,
    val params: MutableMap<String, @Serializable(with = AnyValueSerializer::class) Any> = mutableMapOf(),
) {
    /** Adds or replaces a string parameter. */
    fun param(key: String, value: String): GaEvent = apply { params[key] = value }

    /** Adds or replaces an integer parameter. */
    fun param(key: String, value: Int): GaEvent = apply { params[key] = value }

    /** Adds or replaces a long parameter. */
    fun param(key: String, value: Long): GaEvent = apply { params[key] = value }

    /** Adds or replaces a double parameter. */
    fun param(key: String, value: Double): GaEvent = apply { params[key] = value }

    /** Adds or replaces a boolean parameter. */
    fun param(key: String, value: Boolean): GaEvent = apply { params[key] = value }

    /** Bulk-adds parameters from a map. */
    fun params(map: Map<String, Any>): GaEvent = apply { params.putAll(map) }
}
