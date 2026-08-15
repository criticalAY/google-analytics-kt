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

package com.criticalay

/**
 * Identifies a GA4 session.
 *
 * Wraps a [String] rather than a number so gtag-style epoch seconds and opaque ids
 * are both expressible, and so the value can't be confused with the other identifier
 * strings a hit carries.
 *
 * @property value sent verbatim as the `session_id` event parameter
 */
@JvmInline
value class SessionId(
    val value: String,
) {
    init {
        require(value.isNotBlank()) { "SessionId must not be blank" }
    }

    override fun toString(): String = value

    companion object {
        /** Builds an id from Unix epoch seconds, the format gtag.js uses. */
        fun fromEpochSeconds(epochSeconds: Long): SessionId = SessionId(epochSeconds.toString())
    }
}
