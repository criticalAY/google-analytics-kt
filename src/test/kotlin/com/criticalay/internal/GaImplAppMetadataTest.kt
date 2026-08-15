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

package com.criticalay.internal

import com.criticalay.GoogleAnalyticsConfig
import com.criticalay.httpclient.OkHttpClientImpl
import com.criticalay.request.GaEvent
import com.criticalay.request.GaRequest
import com.criticalay.request.UserPropertyValue
import com.criticalay.response.GaResponse
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class GaImplAppMetadataTest {
    private fun config(
        appName: String? = null,
        appVersion: String? = null,
    ) = GoogleAnalyticsConfig(
        measurementId = "G-TEST",
        apiSecret = "secret",
        appName = appName,
        appVersion = appVersion,
    )

    private fun hit() = GaRequest(clientId = "client", events = listOf(GaEvent("test_event")))

    @Test
    fun `configured app name and version are sent as user properties`() =
        runBlocking {
            val sent = sendCapturing(config(appName = "AnkiDroid", appVersion = "2.25.0"), hit())

            assertEquals("AnkiDroid", sent.userProperties["application_name"]?.value)
            assertEquals("2.25.0", sent.userProperties["application_version"]?.value)
        }

    @Test
    fun `nothing is added when the config leaves them unset`() =
        runBlocking {
            val sent = sendCapturing(config(), hit())

            assertNull(sent.userProperties["application_name"])
            assertNull(sent.userProperties["application_version"])
        }

    @Test
    fun `a caller-supplied property of the same name wins`() =
        runBlocking {
            val explicit =
                GaRequest(
                    clientId = "client",
                    userProperties = mapOf("application_name" to UserPropertyValue("Explicit")),
                    events = listOf(GaEvent("test_event")),
                )

            val sent = sendCapturing(config(appName = "AnkiDroid"), explicit)

            assertEquals("Explicit", sent.userProperties["application_name"]?.value)
        }

    private suspend fun sendCapturing(
        config: GoogleAnalyticsConfig,
        request: GaRequest,
    ): GaRequest {
        val httpClient = mockk<OkHttpClientImpl>()
        val captured = slot<GaRequest>()
        coEvery { httpClient.post(capture(captured)) } returns GaResponse(statusCode = 204)
        GaImpl(config, httpClient).send(request)
        return captured.captured
    }
}
