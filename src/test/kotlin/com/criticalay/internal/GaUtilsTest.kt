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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GaUtilsTest {

    // ---------- isBlank / isNotBlank ----------

    @Test
    fun `isBlank returns true for null, empty, and whitespace`() {
        assertTrue(GaUtils.isBlank(null))
        assertTrue(GaUtils.isBlank(""))
        assertTrue(GaUtils.isBlank("   "))
        assertTrue(GaUtils.isBlank("\n\t"))
    }

    @Test
    fun `isBlank returns false for non-blank strings`() {
        assertFalse(GaUtils.isBlank("a"))
        assertFalse(GaUtils.isBlank(" a "))
    }

    @Test
    fun `isNotBlank is the inverse of isBlank`() {
        assertFalse(GaUtils.isNotBlank(null))
        assertFalse(GaUtils.isNotBlank(""))
        assertFalse(GaUtils.isNotBlank("   "))
        assertTrue(GaUtils.isNotBlank("x"))
    }

    // ---------- firstNotNull ----------

    @Test
    fun `firstNotNull returns the first non-null value`() {
        assertEquals("a", GaUtils.firstNotNull(null, "a", "b"))
        assertEquals(42, GaUtils.firstNotNull(null, null, 42, 7))
    }

    @Test
    fun `firstNotNull returns null when all values are null`() {
        assertNull(GaUtils.firstNotNull<String>(null, null))
    }

    @Test
    fun `firstNotNull returns null when called with no arguments`() {
        assertNull(GaUtils.firstNotNull<String>())
    }

    // ---------- validateEventName ----------

    @Test
    fun `validateEventName accepts valid names`() {
        GaUtils.validateEventName("page_view")
        GaUtils.validateEventName("event1")
        GaUtils.validateEventName("a")
        GaUtils.validateEventName("My_Event_42")
    }

    @Test
    fun `validateEventName rejects blank names`() {
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("   ") }
    }

    @Test
    fun `validateEventName rejects names longer than 40 characters`() {
        val tooLong = "a".repeat(41)
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName(tooLong) }
    }

    @Test
    fun `validateEventName accepts a name of exactly 40 characters`() {
        val exact = "a".repeat(40)
        GaUtils.validateEventName(exact)
    }

    @Test
    fun `validateEventName rejects names not starting with a letter`() {
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("1event") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("_event") }
    }

    @Test
    fun `validateEventName rejects names with invalid characters`() {
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("event-name") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("event name") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("event.name") }
    }

    @Test
    fun `validateEventName rejects reserved prefixes`() {
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("ga_session") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("google_signal") }
        assertThrows<IllegalArgumentException> { GaUtils.validateEventName("firebase_event") }
    }

    // ---------- nowMicros ----------

    @Test
    fun `nowMicros returns time in microseconds`() {
        val before = System.currentTimeMillis() * 1_000L
        val micros = GaUtils.nowMicros()
        val after = System.currentTimeMillis() * 1_000L
        assertTrue(micros in before..after, "expected $before <= $micros <= $after")
    }
}
