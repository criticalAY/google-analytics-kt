package com.criticalay.request

import com.criticalay.GoogleAnalytics


/**
 * Hit builder for the GA4 `screen_view` event.
 *
 * Used in mobile/app analytics to record which screen the user is on.
 *
 * Usage:
 * ```kotlin
 * ga.screenView("client-id-123")
 *   .screenName("HomeScreen")
 *   .appName("MyApp")
 *   .send()
 * ```
 */
class ScreenViewHit(clientId: String, ga: GoogleAnalytics)
    : BaseHit<ScreenViewHit>("screen_view", clientId, ga) {

    /** The screen name or identifier the user is currently viewing. */
    fun screenName(name: String): ScreenViewHit = apply { event.param("screen_name", name) }

    /** The name of the application. */
    fun appName(name: String): ScreenViewHit = apply { event.param("app_name", name) }

    /** The version of the application. */
    fun appVersion(version: String): ScreenViewHit = apply { event.param("app_version", version) }
}
