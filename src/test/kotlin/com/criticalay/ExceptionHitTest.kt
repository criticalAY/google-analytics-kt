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

import com.criticalay.request.ExceptionHit
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ExceptionHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `description and fatal flag map to GA4 parameters`() {
        val hit =
            ExceptionHit("client-1", mockGa)
                .description("NPE at Foo.kt:42")
                .fatal(true)

        val params = hit.event.params
        assertEquals("NPE at Foo.kt:42", params["description"])
        assertEquals(true, params["fatal"])
    }

    @Test
    fun `exception helper records class name and message without stack by default`() {
        val ex = IllegalStateException("boom")
        val hit = ExceptionHit("client-1", mockGa).exception(ex)

        val description = hit.event.params["description"] as String
        assertTrue(description.startsWith("IllegalStateException: boom"), description)
        assertTrue(!description.contains(" @ "), "description should not include stack: $description")
    }

    @Test
    fun `exception helper includes truncated stack when requested`() {
        val ex = RuntimeException("kaboom")
        val hit = ExceptionHit("client-1", mockGa).exception(ex, includeStack = true)

        val description = hit.event.params["description"] as String
        assertTrue(description.contains("RuntimeException: kaboom @ "), description)
    }

    @Test
    fun `exception helper truncates description to 500 characters`() {
        val longMessage = "x".repeat(1000)
        val ex = RuntimeException(longMessage)
        val hit = ExceptionHit("client-1", mockGa).exception(ex)

        val description = hit.event.params["description"] as String
        assertEquals(500, description.length)
    }

    @Test
    fun `exception event uses the GA4 exception name`() {
        val hit = ExceptionHit("client-1", mockGa)
        assertEquals("exception", hit.event.name)
        assertNotNull(hit)
    }
}
