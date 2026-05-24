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
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CustomHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `custom hit uses the supplied event name`() {
        val hit = CustomHit("client-1", "tool_used", mockGa)
        assertEquals("tool_used", hit.event.name)
    }

    @Test
    fun `custom hit accepts arbitrary parameters via inherited param methods`() {
        val hit = CustomHit("client-1", "tool_used", mockGa)
            .param("tool_name", "brush")
            .param("duration_ms", 3400L)
            .param("canvas_width", 1920)
            .param("flag", true)
            .param("ratio", 1.5)

        val params = hit.event.params
        assertEquals("brush", params["tool_name"])
        assertEquals(3400L, params["duration_ms"])
        assertEquals(1920, params["canvas_width"])
        assertEquals(true, params["flag"])
        assertEquals(1.5, params["ratio"])
    }
}
