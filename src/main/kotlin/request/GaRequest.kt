package com.criticalay.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The top-level GA4 Measurement Protocol request body.
 *
 * Serializes directly to the JSON body that is POST-ed to the GA4
 * Measurement Protocol endpoint.
 *
 * Reference: https://developers.google.com/analytics/devguides/collection/protocol/ga4/reference
 *
 * @param clientId        Unique identifier for a user device/browser.
 *                        Must be set for web streams. Use [appInstanceId] for app streams.
 * @param appInstanceId   Unique identifier for a Firebase App Instance.
 *                        Use instead of [clientId] for Firebase-based app streams.
 * @param userId          Optional known user ID for cross-device tracking.
 * @param timestampMicros Unix timestamp in **microseconds** for the event. When omitted
 *                        the server records the current time. Must not be older than 72 hours.
 * @param userProperties  User-scoped custom properties. Values must be strings or numbers.
 * @param events          The events to record (1–25 per request).
 */
@Serializable
data class GaRequest(
    @SerialName("client_id")
    val clientId: String? = null,

    @SerialName("app_instance_id")
    val appInstanceId: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("timestamp_micros")
    val timestampMicros: Long? = null,

    @SerialName("user_properties")
    val userProperties: Map<String, UserPropertyValue> = emptyMap(),

    val events: List<GaEvent> = emptyList(),
) {
    init {
        require(clientId != null || appInstanceId != null) {
            "Either clientId or appInstanceId must be provided"
        }
        require(events.isNotEmpty()) { "At least one event is required" }
        require(events.size <= 25)   { "Maximum 25 events per request (GA4 limit)" }
    }
}

/**
 * Wrapper for a single user property value, as required by the GA4 MP JSON schema:
 * ```json
 * "user_properties": { "key": { "value": "..." } }
 * ```
 */
@Serializable
data class UserPropertyValue(
    @SerialName("value")
    val value: String,
)
