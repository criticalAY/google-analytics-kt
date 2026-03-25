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

import com.criticalay.internal.GaImpl
import com.criticalay.httpclient.OkHttpClientImpl

/**
 * Builder for configuring and creating a [GoogleAnalytics] instance.
 *
 * This DSL-style builder collects all configuration required for sending events
 * to Google Analytics 4 (GA4) via the Measurement Protocol.
 *
 * ### Required properties
 * - [measurementId] – The GA4 Measurement ID.
 * - [apiSecret] – The Measurement Protocol API secret.
 *
 * ### Optional configuration
 * The builder also allows customization of:
 * - App metadata ([appName], [appVersion])
 * - Delivery behavior ([enabled], [debug], [samplePercentage])
 * - Batching ([batchingEnabled], [batchSize])
 * - Networking ([endpointUrl], proxy settings, timeouts)
 *
 *
 * ### Example
 * ```kotlin
 * val analytics = GoogleAnalytics4.builder {
 *     measurementId = "G-XXXXXXXX"
 *     apiSecret = "your-secret"
 *     appName = "MyApp"
 *     appVersion = "1.0.0"
 *     batchingEnabled = true
 * }
 * ```
 *
 * Call [build] to create a fully configured [GoogleAnalytics] instance.
 */
class GoogleAnalyticsBuilder {

    /** GA4 Measurement ID (e.g. "G-XXXXXXXX"). **Required.** */
    var measurementId: String = ""

    /** Measurement Protocol API secret. **Required.** */
    var apiSecret: String = ""

    /** Optional application name recorded as a user-property on every event. */
    var appName: String? = null

    /** Optional application version recorded as a user-property on every event. */
    var appVersion: String? = null

    /** Master enable/disable switch (default: true). */
    var enabled: Boolean = true

    /**
     * When true, hits are sent to the GA4 validation (debug) endpoint which
     * returns JSON validation messages rather than recording actual data.
     */
    var debug: Boolean = false

    /**
     * Percentage of SDK instances that will actually send events (1-100).
     * A random number is drawn at construction time; if it exceeds this value
     * the instance silently drops all hits.  Default: 100 (always in-sample).
     */
    var samplePercentage: Int = 100

    /** Enable local event batching (default: false). */
    var batchingEnabled: Boolean = false

    /**
     * Maximum events per POST request when batching is enabled.
     * GA4 enforces a hard limit of 25.  Default: 25.
     */
    var batchSize: Int = 25

    /** Override the standard collect endpoint URL. */
    var endpointUrl: String = GoogleAnalyticsConfig.DEFAULT_ENDPOINT

    /** Optional HTTP proxy host. */
    var proxyHost: String? = null

    /** Optional HTTP proxy port. */
    var proxyPort: Int = 0

    /** OkHttp connection timeout in milliseconds (default: 10 000). */
    var connectTimeoutMs: Long = 10_000L

    /** OkHttp read timeout in milliseconds (default: 30 000). */
    var readTimeoutMs: Long = 30_000L

    /** Builds and returns a fully initialized [GoogleAnalytics] instance. */
    fun build(): GoogleAnalytics {
        val config = GoogleAnalyticsConfig(
            measurementId   = measurementId,
            apiSecret       = apiSecret,
            appName         = appName,
            appVersion      = appVersion,
            enabled         = enabled,
            debug           = debug,
            samplePercentage = samplePercentage,
            batchingEnabled = batchingEnabled,
            batchSize       = batchSize,
            endpointUrl     = endpointUrl,
            proxyHost       = proxyHost,
            proxyPort       = proxyPort,
            connectTimeoutMs = connectTimeoutMs,
            readTimeoutMs   = readTimeoutMs,
        )
        val httpClient = OkHttpClientImpl(config)
        return GaImpl(config, httpClient)
    }
}
