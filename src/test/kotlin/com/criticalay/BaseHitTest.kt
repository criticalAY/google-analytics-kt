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

import com.criticalay.request.EventHit
import com.criticalay.request.UserPropertyValue
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import com.criticalay.request.GaRequest
import com.criticalay.response.GaResponse

class BaseHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `sessionId is stored both internally and as an event parameter`() {
        val hit = EventHit("c1", mockGa).sessionId("session-123")
        assertEquals("session-123", hit.event.params["session_id"])
        assertEquals("session-123", hit.sessionId)
    }

    @Test
    fun `engagementTimeMs is stored both internally and as an event parameter`() {
        val hit = EventHit("c1", mockGa).engagementTimeMs(2500)
        assertEquals(2500L, hit.event.params["engagement_time_msec"])
        assertEquals(2500L, hit.engagementTimeMs)
    }

    @Test
    fun `userId is captured on the hit`() {
        val hit = EventHit("c1", mockGa).userId("user-99")
        assertEquals("user-99", hit.userId)
    }

    @Test
    fun `userProperty stores wrapped values in the userProperties map`() {
        val hit = EventHit("c1", mockGa)
            .userProperty("plan", "premium")
            .userProperty("locale", "en_IN")

        assertEquals(UserPropertyValue("premium"), hit.userProperties["plan"])
        assertEquals(UserPropertyValue("en_IN"), hit.userProperties["locale"])
    }

    @Test
    fun `timestampMicros is stored as-is`() {
        val ts = 1_700_000_000_000_000L
        val hit = EventHit("c1", mockGa).timestampMicros(ts)
        assertEquals(ts, hit.timestampMicros)
    }

    @Test
    fun `param overloads cover all primitive types`() {
        val hit = EventHit("c1", mockGa)
            .param("s", "v")
            .param("i", 1)
            .param("l", 2L)
            .param("d", 3.0)
            .param("b", true)

        val params = hit.event.params
        assertEquals("v", params["s"])
        assertEquals(1, params["i"])
        assertEquals(2L, params["l"])
        assertEquals(3.0, params["d"])
        assertEquals(true, params["b"])
    }

    @Test
    fun `buildRequest fills session_id and engagement_time_msec defaults when missing`() {
        val hit = EventHit("c1", mockGa).category("ui")
        val request = hit.buildRequest()

        val event = request.events.single()
        assertNotNull(event.params["session_id"], "session_id should be auto-populated")
        assertEquals(1L, event.params["engagement_time_msec"])
    }

    @Test
    fun `buildRequest preserves explicit session_id and engagement_time_msec`() {
        val hit = EventHit("c1", mockGa)
            .sessionId("explicit-session")
            .engagementTimeMs(5000L)
        val request = hit.buildRequest()

        val event = request.events.single()
        assertEquals("explicit-session", event.params["session_id"])
        assertEquals(5000L, event.params["engagement_time_msec"])
    }

    @Test
    fun `buildRequest carries clientId, userId, timestamp and user properties`() {
        val hit = EventHit("client-42", mockGa)
            .userId("user-1")
            .timestampMicros(1_700_000_000_000_000L)
            .userProperty("plan", "free")
        val request = hit.buildRequest()

        assertEquals("client-42", request.clientId)
        assertEquals("user-1", request.userId)
        assertEquals(1_700_000_000_000_000L, request.timestampMicros)
        assertEquals(UserPropertyValue("free"), request.userProperties["plan"])
        assertNull(request.appInstanceId)
    }

    @Test
    fun `buildRequest produces exactly one event`() {
        val request = EventHit("c1", mockGa).category("x").buildRequest()
        assertEquals(1, request.events.size)
        assertEquals("event", request.events.single().name)
    }

    @Test
    fun `send delegates to the GoogleAnalytics instance with the built request`() = runBlocking {
        val captured = slot<GaRequest>()
        coEvery { mockGa.send(capture(captured)) } returns GaResponse(statusCode = 204)

        val response = EventHit("c1", mockGa).category("ui").send()

        coVerify(exactly = 1) { mockGa.send(any()) }
        assertEquals(204, response.statusCode)
        assertEquals("c1", captured.captured.clientId)
        assertTrue(captured.captured.events.isNotEmpty())
    }

    @Test
    fun `sendAsync delegates to the GoogleAnalytics instance`() {
        EventHit("c1", mockGa).category("ui").sendAsync()
        verify(exactly = 1) { mockGa.sendAsync(any()) }
    }
}
