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


/**
 * Configuration for the GA4 Measurement Protocol SDK.
 *
 * @param measurementId  Your GA4 Measurement ID (e.g. "G-XXXXXXXX"), found in
 *                       GA Admin > Data Streams > your stream.
 * @param apiSecret      The Measurement Protocol API secret, created in
 *                       GA Admin > Data Streams > your stream > Measurement Protocol API secrets.
 * @param appName        Optional app/product name appended to every event as a user-property.
 * @param appVersion     Optional app version appended to every event as a user-property.
 * @param enabled        Master switch — when false all send calls are silently dropped.
 * @param debug          When true, hits are sent to the GA4 validation endpoint
 *                       (https://www.google-analytics.com/debug/mp/collect) which returns
 *                       validation messages instead of recording data.
 * @param samplePercentage  A value 1-100. The SDK elects at construction time whether this
 *                          instance is "in-sample". If not in-sample, all hits are dropped.
 * @param batchingEnabled   When true, events are accumulated locally and flushed when
 *                          [batchSize] is reached or [flush()] is called.
 * @param batchSize         Maximum events per POST. GA4 allows a maximum of 25.
 * @param endpointUrl       Override the standard collect endpoint.
 * @param proxyHost         Optional HTTP proxy host.
 * @param proxyPort         Optional HTTP proxy port.
 * @param connectTimeoutMs  OkHttp connection timeout in milliseconds.
 * @param readTimeoutMs     OkHttp read timeout in milliseconds.
 */
data class GoogleAnalyticsConfig(
    val measurementId: String,
    val apiSecret: String,
    val appName: String? = null,
    val appVersion: String? = null,
    val enabled: Boolean = true,
    val debug: Boolean = false,
    val samplePercentage: Int = 100,
    val batchingEnabled: Boolean = false,
    val batchSize: Int = 25,
    val endpointUrl: String = DEFAULT_ENDPOINT,
    val proxyHost: String? = null,
    val proxyPort: Int = 0,
    val connectTimeoutMs: Long = 10_000L,
    val readTimeoutMs: Long = 30_000L,
) {
    companion object {
        const val DEFAULT_ENDPOINT = "https://www.google-analytics.com/mp/collect"
        const val DEBUG_ENDPOINT   = "https://www.google-analytics.com/debug/mp/collect"
    }

    /** Returns the effective endpoint URL (debug or standard). */
    fun effectiveEndpointUrl(): String = if (debug) DEBUG_ENDPOINT else endpointUrl

    init {
        require(measurementId.isNotBlank()) { "measurementId must not be blank" }
        require(apiSecret.isNotBlank())     { "apiSecret must not be blank" }
        require(samplePercentage in 1..100) { "samplePercentage must be between 1 and 100" }
        require(batchSize in 1..25)         { "batchSize must be between 1 and 25 (GA4 limit)" }
    }
}
