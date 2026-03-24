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
