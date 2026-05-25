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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class GaRequestTest {
    private fun event(name: String = "test") = GaEvent(name)

    @Test
    fun `should build a valid request with clientId and one event`() {
        val req = GaRequest(clientId = "c1", events = listOf(event()))
        assertEquals("c1", req.clientId)
        assertEquals(1, req.events.size)
    }

    @Test
    fun `should accept appInstanceId in place of clientId`() {
        val req = GaRequest(appInstanceId = "app-1", events = listOf(event()))
        assertEquals("app-1", req.appInstanceId)
    }

    @Test
    fun `should reject when both clientId and appInstanceId are null`() {
        assertThrows<IllegalArgumentException> {
            GaRequest(events = listOf(event()))
        }
    }

    @Test
    fun `should reject when events list is empty`() {
        assertThrows<IllegalArgumentException> {
            GaRequest(clientId = "c1", events = emptyList())
        }
    }

    @Test
    fun `should reject when events list exceeds 25`() {
        val events = List(26) { event("e$it") }
        assertThrows<IllegalArgumentException> {
            GaRequest(clientId = "c1", events = events)
        }
    }

    @Test
    fun `should accept exactly 25 events`() {
        val events = List(25) { event("e$it") }
        assertDoesNotThrow {
            GaRequest(clientId = "c1", events = events)
        }
    }

    @Test
    fun `userProperties default to empty map`() {
        val req = GaRequest(clientId = "c1", events = listOf(event()))
        assertEquals(emptyMap<String, UserPropertyValue>(), req.userProperties)
    }

    @Test
    fun `userPropertyValue holds the provided string`() {
        val value = UserPropertyValue("premium")
        assertEquals("premium", value.value)
    }
}
