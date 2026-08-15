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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class SessionIdTest {
    @Test
    fun `blank ids are rejected`() {
        assertThrows<IllegalArgumentException> { SessionId("") }
        assertThrows<IllegalArgumentException> { SessionId("   ") }
    }

    @Test
    fun `fromEpochSeconds renders the gtag format`() {
        assertEquals("1700000000", SessionId.fromEpochSeconds(1_700_000_000).value)
    }

    @Test
    fun `toString is the raw value, so it can be used as a param`() {
        assertEquals("abc", SessionId("abc").toString())
    }
}
