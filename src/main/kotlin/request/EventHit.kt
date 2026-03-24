package com.criticalay.request

import com.criticalay.GoogleAnalytics

/**
 * Builder for a GA4 `event`.
 *
 * Supports optional category/action/label/value fields for easier migration
 * from Universal Analytics. In GA4, these are sent as custom parameters.
 *
 * Example:
 * ```kotlin
 * ga.event("client-id")
 *   .category("feature")
 *   .action("opened")
 *   .label("dark_mode")
 *   .value(1)
 *   .send()
 * ```
 */
class EventHit(clientId: String, ga: GoogleAnalytics)
    : BaseHit<EventHit>("event", clientId, ga) {
    
    fun eventName(name: String): EventHit = apply {
        // Mutate the underlying Ga4Event's name via a replacement — we copy into params
        // and set a special internal key used by Ga4Impl to override the name.
        event.param("_event_name_override", name)
    }

    /** The event category (stored as the `event_category` parameter). */
    fun category(value: String): EventHit = apply { event.param("event_category", value) }

    /** The event action (stored as the `event_action` parameter). */
    fun action(value: String): EventHit = apply { event.param("event_action", value) }

    /** The event label (stored as the `event_label` parameter). */
    fun label(value: String): EventHit = apply { event.param("event_label", value) }

    /** An optional numeric value for the event (stored as `value`). */
    fun value(v: Int): EventHit = apply { event.param("value", v) }

    /** An optional numeric value for the event (stored as `value`). */
    fun value(v: Long): EventHit = apply { event.param("value", v) }
}
