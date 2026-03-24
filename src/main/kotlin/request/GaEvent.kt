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
