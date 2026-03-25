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
 * Builder for the GA4 `timing_complete` event.
 *
 * Used to record the duration of operations (e.g., API calls, rendering, I/O).
 *
 * Example:
 * ```
 * ga.timing("client-id")
 *   .timingCategory("api")
 *   .timingName("fetchUser")
 *   .timingValue(342)
 *   .send()
 * ```
 */
class TimingHit(clientId: String, ga: GoogleAnalytics)
    : BaseHit<TimingHit>("timing_complete", clientId, ga) {

    /**
     * A category for the timed operation (e.g. "api", "db", "render").
     * Stored in the `event_category` parameter.
     */
    fun timingCategory(value: String): TimingHit = apply { event.param("event_category", value) }

    /**
     * The variable / label being timed (e.g. "fetchUser", "loadImage").
     * Stored in the `name` parameter.
     */
    fun timingName(value: String): TimingHit = apply { event.param("name", value) }

    /**
     * The elapsed time in **milliseconds**.
     * Stored in the `value` parameter.
     */
    fun timingValue(millis: Int): TimingHit = apply { event.param("value", millis) }

    /**
     * The elapsed time in **milliseconds** (Long overload).
     * Stored in the `value` parameter.
     */
    fun timingValue(millis: Long): TimingHit = apply { event.param("value", millis) }

    /**
     * An optional label for further disambiguation.
     * Stored in the `event_label` parameter.
     */
    fun timingLabel(value: String): TimingHit = apply { event.param("event_label", value) }
}
