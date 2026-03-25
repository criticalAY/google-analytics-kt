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

import com.criticalay.GoogleAnalytics


/**
 * Hit builder for the GA4 `page_view` event.
 *
 * Usage:
 * ```kotlin
 * ga.pageView("client-id-123")
 *   .pageLocation("https://myapp.com/home")
 *   .pageTitle("Home")
 *   .sessionId("session-abc")
 *   .engagementTimeMs(500)
 *   .send()
 * ```
 */
class PageViewHit(clientId: String, ga: GoogleAnalytics)
    : BaseHit<PageViewHit>("page_view", clientId, ga) {

    /**
     * Full URL of the page being viewed.
     * Equivalent to the `dl` (document location) parameter in UA.
     */
    fun pageLocation(url: String): PageViewHit = apply { event.param("page_location", url) }

    /**
     * Title of the page being viewed.
     * Equivalent to the `dt` (document title) parameter in UA.
     */
    fun pageTitle(title: String): PageViewHit = apply { event.param("page_title", title) }

    /**
     * The referrer URL.
     * Equivalent to the `dr` (document referrer) parameter in UA.
     */
    fun pageReferrer(referrer: String): PageViewHit = apply { event.param("page_referrer", referrer) }
}
