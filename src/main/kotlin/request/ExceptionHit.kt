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
 * Hit builder for the GA4 `exception` event (analogous to UA's Exception hit).
 *
 * Usage:
 * ```kotlin
 * ga.exception("client-id-123")
 *   .description("NullPointerException at Foo.kt:42")
 *   .fatal(true)
 *   .send()
 * ```
 */
class ExceptionHit(clientId: String, ga: GoogleAnalytics)
    : BaseHit<ExceptionHit>("exception", clientId, ga) {

    /**
     * A description of the exception (max 150 characters in UA, no hard limit in GA4).
     * Stored in the `description` event parameter.
     */
    fun description(value: String): ExceptionHit = apply { event.param("description", value) }

    /**
     * Convenience overload that records the exception message and optionally the
     * abbreviated stack trace as the description.
     */
    fun exception(e: Throwable, includeStack: Boolean = false): ExceptionHit = apply {
        val desc = if (includeStack) {
            val stack = e.stackTrace.take(5).joinToString(" | ") { it.toString() }
            "${e::class.simpleName}: ${e.message} @ $stack"
        } else {
            "${e::class.simpleName}: ${e.message}"
        }
        event.param("description", desc.take(500))
    }

    /**
     * Whether the exception was fatal (caused the application to crash).
     * Stored in the `fatal` event parameter.
     */
    fun fatal(value: Boolean): ExceptionHit = apply { event.param("fatal", value) }
}
