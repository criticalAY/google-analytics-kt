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
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

class EventHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `test event hit mapping to parameters`() {
        val eventHit = EventHit("client-id", mockGa)
            .category("feature")
            .action("opened")
            .label("dark_mode")
            .value(10)

        val params = eventHit.event.params

        assertEquals("feature", params["event_category"])
        assertEquals("opened", params["event_action"])
        assertEquals("dark_mode", params["event_label"])
        assertEquals(10, params["value"])
    }
}