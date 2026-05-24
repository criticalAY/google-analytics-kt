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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GoogleAnalyticsConfigTest {

    private fun validConfig(
        measurementId: String = "G-TEST",
        apiSecret: String = "secret",
        samplePercentage: Int = 100,
        batchSize: Int = 25,
        debug: Boolean = false,
        endpointUrl: String = GoogleAnalyticsConfig.DEFAULT_ENDPOINT,
    ) = GoogleAnalyticsConfig(
        measurementId = measurementId,
        apiSecret = apiSecret,
        samplePercentage = samplePercentage,
        batchSize = batchSize,
        debug = debug,
        endpointUrl = endpointUrl,
    )

    @Test
    fun `should accept a fully valid config`() {
        val config = validConfig()
        assertEquals("G-TEST", config.measurementId)
        assertEquals("secret", config.apiSecret)
    }

    @Test
    fun `should reject blank measurementId`() {
        assertThrows<IllegalArgumentException> { validConfig(measurementId = "") }
        assertThrows<IllegalArgumentException> { validConfig(measurementId = "   ") }
    }

    @Test
    fun `should reject blank apiSecret`() {
        assertThrows<IllegalArgumentException> { validConfig(apiSecret = "") }
        assertThrows<IllegalArgumentException> { validConfig(apiSecret = "   ") }
    }

    @Test
    fun `should reject samplePercentage out of 1 to 100`() {
        assertThrows<IllegalArgumentException> { validConfig(samplePercentage = 0) }
        assertThrows<IllegalArgumentException> { validConfig(samplePercentage = -1) }
        assertThrows<IllegalArgumentException> { validConfig(samplePercentage = 101) }
    }

    @Test
    fun `should accept samplePercentage boundary values`() {
        assertEquals(1, validConfig(samplePercentage = 1).samplePercentage)
        assertEquals(100, validConfig(samplePercentage = 100).samplePercentage)
    }

    @Test
    fun `should reject batchSize out of 1 to 25`() {
        assertThrows<IllegalArgumentException> { validConfig(batchSize = 0) }
        assertThrows<IllegalArgumentException> { validConfig(batchSize = 26) }
        assertThrows<IllegalArgumentException> { validConfig(batchSize = -5) }
    }

    @Test
    fun `should accept batchSize boundary values`() {
        assertEquals(1, validConfig(batchSize = 1).batchSize)
        assertEquals(25, validConfig(batchSize = 25).batchSize)
    }

    @Test
    fun `effectiveEndpointUrl should return standard endpoint when debug is false`() {
        val config = validConfig(debug = false)
        assertEquals(GoogleAnalyticsConfig.DEFAULT_ENDPOINT, config.effectiveEndpointUrl())
    }

    @Test
    fun `effectiveEndpointUrl should return debug endpoint when debug is true`() {
        val config = validConfig(debug = true)
        assertEquals(GoogleAnalyticsConfig.DEBUG_ENDPOINT, config.effectiveEndpointUrl())
    }

    @Test
    fun `debug endpoint constant should differ from default endpoint`() {
        assertNotEquals(GoogleAnalyticsConfig.DEFAULT_ENDPOINT, GoogleAnalyticsConfig.DEBUG_ENDPOINT)
    }

    @Test
    fun `effectiveEndpointUrl should ignore custom endpointUrl when debug is true`() {
        val config = validConfig(
            debug = true,
            endpointUrl = "https://example.com/collect",
        )
        assertEquals(GoogleAnalyticsConfig.DEBUG_ENDPOINT, config.effectiveEndpointUrl())
    }

    @Test
    fun `should preserve custom endpoint when debug is false`() {
        val custom = "https://example.com/collect"
        val config = validConfig(debug = false, endpointUrl = custom)
        assertEquals(custom, config.effectiveEndpointUrl())
    }
}
