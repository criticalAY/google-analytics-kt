package com.criticalay

import com.criticalay.request.AnyValueSerializer
import io.mockk.mockk
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.contextual
import kotlinx.serialization.serializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class AnyValueSerializerTest {
    private val json = Json {
        serializersModule = SerializersModule {
            contextual(AnyValueSerializer)
        }
    }

    @Test
    fun `should serialize various types to JSON primitives`() {
        assertEquals("\"hello\"", json.encodeToString(AnyValueSerializer, "hello"))
        assertEquals("123", json.encodeToString(AnyValueSerializer, 123))
        assertEquals("45.67", json.encodeToString(AnyValueSerializer, 45.67))
        assertEquals("true", json.encodeToString(AnyValueSerializer, true))
        assertEquals("9999999999", json.encodeToString(AnyValueSerializer, 9999999999L))
    }

    @Test
    fun `should deserialize JSON primitives back to Kotlin types`() {
        assertEquals("hello", json.decodeFromString(AnyValueSerializer, "\"hello\""))
        assertEquals(123L, json.decodeFromString(AnyValueSerializer, "123"))
        assertEquals(45.67, json.decodeFromString(AnyValueSerializer, "45.67"))
        assertEquals(true, json.decodeFromString(AnyValueSerializer, "true"))
    }

    @Test
    fun `should fallback to string for unknown types during serialization`() {
        val unknownType = listOf(1, 2, 3)
        val result = json.encodeToString(AnyValueSerializer, unknownType)
        assertEquals("\"[1, 2, 3]\"", result)
    }

    @Test
    fun `should throw error when using non-JSON encoder`() {
        assertThrows<IllegalStateException> {
            AnyValueSerializer.serialize(mockk(relaxed = true), "test")
        }
    }

    /**
     * Helper test to ensure it works inside a Map (common use case for GA4 params)
     */
    @Test
    fun `should work within a map structure`() {
        val params = mapOf<String, @kotlinx.serialization.Contextual Any>(
            "category" to "UI",
            "count" to 42,
            "is_valid" to false
        )

        val jsonOutput = json.encodeToString(
            MapSerializer(
                serializer<String>(),
                AnyValueSerializer
            ),
            params
        )

        val expected = """{"category":"UI","count":42,"is_valid":false}"""
        assertEquals(expected, jsonOutput)
    }
}