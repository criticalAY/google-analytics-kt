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
