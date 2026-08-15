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

import com.criticalay.SessionId
import java.util.concurrent.atomic.AtomicReference

/**
 * Tracks the current GA4 session, starting a new one after a period of inactivity.
 *
 * GA4 requires `session_id` in `event.params` for hits to appear in session-based
 * reports; without it GA4 returns 204 but drops the hit from every report.
 *
 * Sessions end after [DEFAULT_TIMEOUT_MILLIS] without a hit, matching GA4's own
 * default, so a long-lived process no longer reports one endless session.
 */
internal object SessionManager {
    /** GA4 ends a session after 30 minutes of inactivity. */
    const val DEFAULT_TIMEOUT_MILLIS: Long = 30 * 60 * 1000L

    private data class Session(
        val id: SessionId,
        val lastActivityMillis: Long,
    )

    private val current = AtomicReference<Session?>(null)

    /**
     * Wall clock rather than a monotonic one: [System.nanoTime] stalls while an Android
     * device is in deep sleep, which would keep a session alive across exactly the idle
     * stretches that should end it.
     */
    @Volatile
    private var clock: () -> Long = System::currentTimeMillis

    /**
     * The live session id, recording this call as activity.
     *
     * Lock-free because it is reached from [com.criticalay.request.BaseHit.buildRequest],
     * which is not a suspending function and so cannot take the coroutine mutex used elsewhere.
     */
    fun currentSessionId(): SessionId {
        while (true) {
            val now = clock()
            val existing = current.get()
            // a backwards jump gives a negative elapsed, which rotates rather than sticking
            val elapsed = if (existing == null) Long.MAX_VALUE else now - existing.lastActivityMillis
            val next =
                if (elapsed in 0 until DEFAULT_TIMEOUT_MILLIS) {
                    existing!!.copy(lastActivityMillis = now)
                } else {
                    Session(SessionId.fromEpochSeconds(now / 1000L), now)
                }
            if (current.compareAndSet(existing, next)) return next.id
        }
    }

    /** Clears the session, optionally pinning a clock so tests need not sleep. */
    internal fun resetForTesting(clock: () -> Long = System::currentTimeMillis) {
        this.clock = clock
        current.set(null)
    }
}
