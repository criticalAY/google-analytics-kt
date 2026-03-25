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

import com.criticalay.GoogleAnalytics
import com.criticalay.internal.SessionManager
import com.criticalay.response.GaResponse

/**
 * Base class for all typed hit builders (PageViewHit, EventHit, etc.).
 *
 * Holds shared state (clientId, userId, userProperties, sessionId,
 * engagementTimeMs) and provides [send] / [sendAsync] helpers that
 * delegate to the [GoogleAnalytics] instance that created this builder.
 *
 * @param T  The concrete hit subtype — used for fluent chaining.
 * @param eventName  The GA4 event name this hit maps to.
 * @param clientId   Unique device/browser identifier.
 * @param ga         The [GoogleAnalytics] instance to send through.
 */
@Suppress("UNCHECKED_CAST")
abstract class BaseHit<T : BaseHit<T>>(
    internal val eventName: String,
    internal val clientId: String,
    internal val ga: GoogleAnalytics,
) {
    internal val event = GaEvent(name = eventName)
    internal val userProperties: MutableMap<String, UserPropertyValue> = mutableMapOf()
    internal var userId: String? = null
    internal var timestampMicros: Long? = null

    internal var sessionId: String? = null
    internal var engagementTimeMs: Long? = null

    /** Sets the known user ID for cross-device tracking. */
    fun userId(value: String): T = (this as T).also { userId = value }

    /**
     * Session identifier — required for user activity to appear in standard
     * GA4 reports.  Usually a Unix-epoch-second string or UUID.
     */
    fun sessionId(value: String): T = (this as T).also {
        event.param("session_id", value)
        sessionId = value
    }

    /**
     * Engagement time in milliseconds.  Required for sessions to count as
     * "engaged sessions" in GA4 reports.  Minimum recommended value: 1.
     */
    fun engagementTimeMs(value: Long): T = (this as T).also {
        event.param("engagement_time_msec", value)
        engagementTimeMs = value
    }

    /**
     * Unix timestamp in **microseconds** for when the event occurred.
     * Omit to use the server's receive time.  Must not be older than 72 hours.
     */
    fun timestampMicros(value: Long): T = (this as T).also { timestampMicros = value }

    /** Adds a user-scoped custom property. */
    fun userProperty(key: String, value: String): T = (this as T).also {
        userProperties[key] = UserPropertyValue(value)
    }

    /** Adds an arbitrary event-level parameter. */
    fun param(key: String, value: String): T  = (this as T).also { event.param(key, value) }
    fun param(key: String, value: Int): T     = (this as T).also { event.param(key, value) }
    fun param(key: String, value: Long): T    = (this as T).also { event.param(key, value) }
    fun param(key: String, value: Double): T  = (this as T).also { event.param(key, value) }
    fun param(key: String, value: Boolean): T = (this as T).also { event.param(key, value) }

    /** Builds the [GaRequest] from this hit's state. */
    fun buildRequest(): GaRequest {
        if (!event.params.containsKey("session_id")) {
            event.param("session_id", SessionManager.sessionId)
        }
        if (!event.params.containsKey("engagement_time_msec")) {
            event.param("engagement_time_msec", 1L)
        }
        return GaRequest(
            clientId        = clientId,
            userId          = userId,
            timestampMicros = timestampMicros,
            userProperties  = userProperties,
            events          = listOf(event),
        )
    }

    /**
     * Sends the hit **synchronously** (suspending).
     * Returns the [GaResponse] from the server.
     */
    suspend fun send(): GaResponse = ga.send(buildRequest())

    /**
     * Sends the hit **asynchronously** (fire-and-forget).
     * Returns immediately; any errors are logged.
     */
    fun sendAsync() = ga.sendAsync(buildRequest())
}
