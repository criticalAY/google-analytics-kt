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

package com.criticalay

import com.criticalay.request.CustomHit
import com.criticalay.request.EventHit
import com.criticalay.request.ExceptionHit
import com.criticalay.request.GaRequest
import com.criticalay.request.PageViewHit
import com.criticalay.request.ScreenViewHit
import com.criticalay.request.TimingHit
import com.criticalay.response.GaResponse

/**
 * Main API for sending events to Google Analytics 4 (GA4).
 *
 * Instances are created via [builder].
 *
 * Provides factory methods (e.g., [pageView], [event]) to build hits,
 * which are sent using `send()` or `sendAsync()`.
 *
 * Event delivery depends on [inSample] and configuration in [config].
 */
interface GoogleAnalytics : AutoCloseable {

    /** The active configuration. */
    val config: GoogleAnalyticsConfig

    /** True if this instance was elected to be in-sample and will actually send events. */
    val inSample: Boolean

    // Hit-type factory methods
    /**
     * Creates a [PageViewHit] builder pre-configured for [clientId].
     * Call [PageViewHit.send] (or [PageViewHit.sendAsync]) when ready.
     */
    fun pageView(clientId: String): PageViewHit

    /** Creates an [EventHit] builder pre-configured for [clientId]. */
    fun event(clientId: String): EventHit

    /** Creates a [ScreenViewHit] builder pre-configured for [clientId]. */
    fun screenView(clientId: String): ScreenViewHit

    /** Creates an [ExceptionHit] builder pre-configured for [clientId]. */
    fun exception(clientId: String): ExceptionHit

    /** Creates a [TimingHit] builder pre-configured for [clientId]. */
    fun timing(clientId: String): TimingHit

    /**
     * Creates a [CustomHit] builder for an arbitrary GA4 event name.
     * The [name] must follow GA4 naming rules (letters, digits, underscores, max 40 chars).
     */
    fun custom(clientId: String, name: String): CustomHit

    /**
     * Sends a fully constructed [GaRequest] synchronously.
     * Blocks until the HTTP response is received.
     */
    suspend fun send(request: GaRequest): GaResponse

    /**
     * Sends a fully constructed [GaRequest] asynchronously (fire-and-forget).
     * Returns immediately; errors are logged.
     */
    fun sendAsync(request: GaRequest)

    /** Flushes any pending batched events immediately. */
    suspend fun flush()

    /** Re-runs the sampling election and returns the new in-sample status. */
    fun performSamplingElection(): Boolean

    /** Releases all resources (coroutine scope, HTTP client). */
    override fun close()

    companion object {
        /**
         * Creates a new [GoogleAnalytics] instance using a DSL builder.
         *
         * ```kotlin
         * val ga = GoogleAnalytics4.builder {
         *     measurementId = "G-XXXXXXXX"
         *     apiSecret     = "your-api-secret"
         *     appName       = "MyApp"
         *     debug         = false
         * }
         * ```
         */
        fun builder(block: GoogleAnalyticsBuilder.() -> Unit): GoogleAnalytics =
            GoogleAnalyticsBuilder().apply(block).build()
    }
}