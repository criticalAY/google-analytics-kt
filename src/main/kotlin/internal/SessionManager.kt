package com.criticalay.internal

/**
 * Generates and holds a single GA4 session ID for the lifetime of the JVM process.
 *
 * GA4 Measurement Protocol requires `session_id` inside `event.params` for events
 * to appear in session-based reports (including Realtime). Without it, GA4 accepts
 * the hit (returns 204) but silently drops it from all reports.
 *
 * The session ID is a Unix timestamp in **seconds** — this is the format GA4 expects
 * and matches the format used by the gtag.js SDK.
 *
 * Thread-safe via Kotlin's `by lazy` with default `LazyThreadSafetyMode.SYNCHRONIZED`.
 */
internal object SessionManager {
    val sessionId: String by lazy {
        (System.currentTimeMillis() / 1000L).toString()
    }
}
