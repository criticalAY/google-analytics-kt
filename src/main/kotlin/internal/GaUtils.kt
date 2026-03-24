package com.criticalay.internal

/**
 * Lightweight utility functions for the GA4 SDK.
 */
internal object GaUtils {

    /** Returns true if the string is null, empty, or blank. */
    fun isBlank(value: String?): Boolean = value.isNullOrBlank()

    /** Returns true if the string is non-null and non-blank. */
    fun isNotBlank(value: String?): Boolean = !isBlank(value)

    /**
     * Returns the first non-null value from the provided arguments,
     * or null if all are null.
     */
    fun <T> firstNotNull(vararg values: T?): T? = values.firstOrNull { it != null }

    /**
     * Validates a GA4 event name against the platform rules:
     * - Max 40 characters
     * - Must start with a letter
     * - Only letters, digits, and underscores
     * - Must not start with a reserved prefix (ga_, google_, firebase_)
     *
     * @throws IllegalArgumentException if the name is invalid.
     */
    fun validateEventName(name: String) {
        require(name.isNotBlank())           { "Event name must not be blank" }
        require(name.length <= 40)           { "Event name must not exceed 40 characters: '$name'" }
        require(name[0].isLetter())          { "Event name must start with a letter: '$name'" }
        require(name.all { it.isLetterOrDigit() || it == '_' }) {
            "Event name must contain only letters, digits, and underscores: '$name'"
        }
        val reserved = listOf("ga_", "google_", "firebase_")
        require(reserved.none { name.startsWith(it) }) {
            "Event name must not start with a reserved prefix (${reserved.joinToString()}): '$name'"
        }
    }

    /**
     * Returns the current Unix time in **microseconds**.
     */
    fun nowMicros(): Long = System.currentTimeMillis() * 1_000L
}
