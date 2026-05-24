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

package com.criticalay.response

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GaResponseTest {

    @Test
    fun `isSuccess is true for 2xx status codes`() {
        assertTrue(GaResponse(statusCode = 200).isSuccess)
        assertTrue(GaResponse(statusCode = 204).isSuccess)
        assertTrue(GaResponse(statusCode = 299).isSuccess)
    }

    @Test
    fun `isSuccess is false for non-2xx status codes`() {
        assertFalse(GaResponse(statusCode = 0).isSuccess)
        assertFalse(GaResponse(statusCode = 199).isSuccess)
        assertFalse(GaResponse(statusCode = 300).isSuccess)
        assertFalse(GaResponse(statusCode = 400).isSuccess)
        assertFalse(GaResponse(statusCode = 500).isSuccess)
        assertFalse(GaResponse(statusCode = -1).isSuccess)
    }

    @Test
    fun `isValid is true when validation messages list is empty`() {
        assertTrue(GaResponse().isValid)
        assertTrue(GaResponse(statusCode = 400).isValid)
    }

    @Test
    fun `isValid is false when there are validation messages`() {
        val resp = GaResponse(
            statusCode = 200,
            validationMessages = listOf(
                ValidationMessage("events[0].name", "Reserved", "NAME_RESERVED")
            ),
        )
        assertFalse(resp.isValid)
    }

    @Test
    fun `default response has empty body and no validation messages`() {
        val resp = GaResponse()
        assertEquals(0, resp.statusCode)
        assertEquals("", resp.requestBody)
        assertTrue(resp.validationMessages.isEmpty())
    }

    @Test
    fun `ValidationMessage exposes its fields`() {
        val msg = ValidationMessage(
            fieldPath = "events[0].name",
            description = "Invalid",
            validationCode = "BAD_NAME",
        )
        assertEquals("events[0].name", msg.fieldPath)
        assertEquals("Invalid", msg.description)
        assertEquals("BAD_NAME", msg.validationCode)
    }
}
