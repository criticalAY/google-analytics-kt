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

import com.criticalay.request.ScreenViewHit
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ScreenViewHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `screen view hit maps fields to GA4 parameters`() {
        val hit =
            ScreenViewHit("client-1", mockGa)
                .screenName("HomeScreen")
                .appName("MyApp")
                .appVersion("1.2.3")

        val params = hit.event.params
        assertEquals("HomeScreen", params["screen_name"])
        assertEquals("MyApp", params["app_name"])
        assertEquals("1.2.3", params["app_version"])
    }

    @Test
    fun `screen view event uses the GA4 screen_view name`() {
        val hit = ScreenViewHit("client-1", mockGa)
        assertEquals("screen_view", hit.event.name)
    }
}
