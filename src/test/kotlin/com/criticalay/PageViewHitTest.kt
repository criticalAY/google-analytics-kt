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

import com.criticalay.request.PageViewHit
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PageViewHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `page view hit maps fields to GA4 parameters`() {
        val hit =
            PageViewHit("client-1", mockGa)
                .pageLocation("https://app.com/home")
                .pageTitle("Home")
                .pageReferrer("https://google.com")

        val params = hit.event.params
        assertEquals("https://app.com/home", params["page_location"])
        assertEquals("Home", params["page_title"])
        assertEquals("https://google.com", params["page_referrer"])
    }

    @Test
    fun `page view event uses the GA4 page_view name`() {
        val hit = PageViewHit("client-1", mockGa)
        assertEquals("page_view", hit.event.name)
    }
}
