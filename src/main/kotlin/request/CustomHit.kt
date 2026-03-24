package com.criticalay.request

import com.criticalay.GoogleAnalytics


/**
 * Hit builder for a fully custom GA4 event with an arbitrary name.
 *
 * This is the most flexible hit type — any valid GA4 event name is accepted
 * and any parameters can be added via the [BaseHit.param] family of methods inherited
 * from [BaseHit].
 *
 * GA4 event name rules:
 * - Must begin with a letter
 * - Only letters, digits, and underscores
 * - Maximum 40 characters
 * - Must not start with `ga_`, `google_`, `firebase_` (reserved prefixes)
 *
 * Usage:
 * ```kotlin
 * ga.custom("client-id-123", "tool_used")
 *   .param("tool_name", "brush")
 *   .param("duration_ms", 3400L)
 *   .param("canvas_width", 1920)
 *   .sessionId("session-abc")
 *   .send()
 * ```
 */
class CustomHit(clientId: String, eventName: String, ga: GoogleAnalytics)
    : BaseHit<CustomHit>(eventName, clientId, ga)
