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

import com.criticalay.request.TimingHit
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TimingHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `timing hit maps category, name, value and label to GA4 parameters`() {
        val hit =
            TimingHit("client-1", mockGa)
                .timingCategory("api")
                .timingName("fetchUser")
                .timingValue(342)
                .timingLabel("logged-in")

        val params = hit.event.params
        assertEquals("api", params["event_category"])
        assertEquals("fetchUser", params["name"])
        assertEquals(342, params["value"])
        assertEquals("logged-in", params["event_label"])
    }

    @Test
    fun `timing value supports long overload`() {
        val hit = TimingHit("client-1", mockGa).timingValue(9_000_000_000L)
        assertEquals(9_000_000_000L, hit.event.params["value"])
    }

    @Test
    fun `timing event uses the GA4 timing_complete name`() {
        val hit = TimingHit("client-1", mockGa)
        assertEquals("timing_complete", hit.event.name)
    }
}
