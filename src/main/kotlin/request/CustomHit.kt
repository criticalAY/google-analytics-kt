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
