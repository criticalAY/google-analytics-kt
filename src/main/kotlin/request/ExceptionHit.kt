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
