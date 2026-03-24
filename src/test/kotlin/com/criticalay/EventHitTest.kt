package com.criticalay

import com.criticalay.request.EventHit
import io.mockk.confirmVerified
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.text.get
import org.junit.jupiter.api.Assertions.assertEquals
import io.mockk.mockk

class EventHitTest {
    private val mockGa = mockk<GoogleAnalytics>(relaxed = true)

    @Test
    fun `test event hit mapping to parameters`() {
        val eventHit = EventHit("client-id", mockGa)
            .category("feature")
            .action("opened")
            .label("dark_mode")
            .value(10)

        val params = eventHit.event.params

        assertEquals("feature", params["event_category"])
        assertEquals("opened", params["event_action"])
        assertEquals("dark_mode", params["event_label"])
        assertEquals(10, params["value"])
    }
}