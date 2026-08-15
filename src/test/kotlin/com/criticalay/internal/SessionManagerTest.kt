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

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class SessionManagerTest {
    private var now = 1_700_000_000_000L

    @BeforeEach
    fun setUp() {
        SessionManager.resetForTesting { now }
    }

    @AfterEach
    fun tearDown() {
        SessionManager.resetForTesting()
    }

    @Test
    fun `the same session is reused while activity continues`() {
        val first = SessionManager.currentSessionId()
        now += SessionManager.DEFAULT_TIMEOUT_MILLIS - 1

        assertEquals(first, SessionManager.currentSessionId())
    }

    @Test
    fun `a new session starts once the timeout has elapsed`() {
        val first = SessionManager.currentSessionId()
        now += SessionManager.DEFAULT_TIMEOUT_MILLIS

        assertNotEquals(first, SessionManager.currentSessionId())
    }

    @Test
    fun `activity keeps the session alive past the timeout`() {
        val first = SessionManager.currentSessionId()
        repeat(4) {
            now += SessionManager.DEFAULT_TIMEOUT_MILLIS / 2
            SessionManager.currentSessionId()
        }

        assertEquals(first, SessionManager.currentSessionId())
    }

    @Test
    fun `a backwards clock jump starts a new session rather than freezing the old one`() {
        val first = SessionManager.currentSessionId()
        now -= 60_000

        assertNotEquals(first, SessionManager.currentSessionId())
    }
}
