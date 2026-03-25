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
