package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DimmingControllerTest {

    private var fakeTimeMs = 0L
    private val timeSource: () -> Long = { fakeTimeMs }

    @Test
    fun startsAtFullOpacity() {
        val dc = DimmingController(timeSource)
        assertEquals(1.0f, dc.opacity.value, 0.001f)
    }

    @Test
    fun zeroCare_fullOpacity() {
        val dc = DimmingController(timeSource)
        dc.update(0f)
        assertEquals(1.0f, dc.opacity.value, 0.001f)
    }

    @Test
    fun fullCare_minimumOpacity() {
        val dc = DimmingController(timeSource)
        fakeTimeMs = 0L
        dc.update(1f)
        for (i in 1..1000) {
            fakeTimeMs = i * 100L
            dc.update(1f)
        }
        val expected = 1.0f - 1.0f * 0.9f
        assertEquals(expected, dc.opacity.value, 0.011f)
    }

    @Test
    fun opacity_neverBelow10Percent() {
        val dc = DimmingController(timeSource)
        fakeTimeMs = 0L
        dc.update(1f)
        for (i in 1..1000) {
            fakeTimeMs = i * 100L
            dc.update(1f)
        }
        assertTrue("opacity too low: ${dc.opacity.value}", dc.opacity.value >= 0.1f)
    }

    @Test
    fun transition_gradual() {
        val dc = DimmingController(timeSource)
        fakeTimeMs = 0L
        dc.update(0.5f)
        for (i in 1..600) {
            fakeTimeMs = i * 100L
            dc.update(0.5f)
        }
        val expectedTarget = 1.0f - 0.5f * 0.9f
        assertTrue(
            "did not reach target after enough ticks: ${dc.opacity.value} vs $expectedTarget",
            kotlin.math.abs(dc.opacity.value - expectedTarget) < 0.02f,
        )
    }

    @Test
    fun reset_restoresFullOpacity() {
        val dc = DimmingController(timeSource)
        fakeTimeMs = 0L
        dc.update(1f)
        for (i in 1..100) {
            fakeTimeMs = i * 10L
            dc.update(0.8f)
        }
        dc.reset()
        assertEquals(1.0f, dc.opacity.value, 0.001f)
    }
}
