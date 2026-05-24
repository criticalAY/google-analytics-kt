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

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GaEventTest {

    @Test
    fun `param methods store values with correct types`() {
        val event = GaEvent("test_event")
            .param("str_key", "hello")
            .param("int_key", 42)
            .param("long_key", 9_000_000_000L)
            .param("double_key", 3.14)
            .param("bool_key", true)

        assertEquals("hello", event.params["str_key"])
        assertEquals(42, event.params["int_key"])
        assertEquals(9_000_000_000L, event.params["long_key"])
        assertEquals(3.14, event.params["double_key"])
        assertEquals(true, event.params["bool_key"])
    }

    @Test
    fun `param overwrites existing key with last value wins`() {
        val event = GaEvent("test_event")
            .param("key", "first")
            .param("key", "second")
            .param("key", 99)

        assertEquals(99, event.params["key"])
        assertEquals(1, event.params.size)
    }

    @Test
    fun `params bulk-add merges into existing params`() {
        val event = GaEvent("test_event")
            .param("existing", "value")
            .params(mapOf("a" to 1, "b" to "two"))

        assertEquals("value", event.params["existing"])
        assertEquals(1, event.params["a"])
        assertEquals("two", event.params["b"])
        assertEquals(3, event.params.size)
    }

    @Test
    fun `params bulk-add overwrites existing keys`() {
        val event = GaEvent("test_event")
            .param("k", "old")
            .params(mapOf("k" to "new"))

        assertEquals("new", event.params["k"])
    }

    @Test
    fun `param methods are chainable and return the same instance`() {
        val event = GaEvent("test_event")
        val result = event.param("a", 1).param("b", "two")
        assertSame(event, result)
    }

    @Test
    fun `new event has an empty params map by default`() {
        val event = GaEvent("test_event")
        assertTrue(event.params.isEmpty())
    }

    @Test
    fun `event keeps its name unchanged`() {
        val event = GaEvent("my_event").param("k", "v")
        assertEquals("my_event", event.name)
    }
}
