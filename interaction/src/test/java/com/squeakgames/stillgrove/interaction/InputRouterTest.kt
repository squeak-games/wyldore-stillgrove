package com.squeakgames.stillgrove.interaction

import com.squeakgames.stillgrove.engine.CareAccumulator
import com.squeakgames.stillgrove.interaction.HandGestureRecognizer.GestureType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class InputRouterTest {

    private lateinit var careAccumulator: CareAccumulator
    private lateinit var router: InputRouter
    private var currentTimeMs: Long = 0L

    @Before
    fun setUp() {
        careAccumulator = CareAccumulator()
        currentTimeMs = 1000L
        router = InputRouter(careAccumulator, timeMs = { currentTimeMs })
    }

    @Test
    fun offering_gesture_appliesOffering() {
        router.routeGesture(GestureType.OFFERING)
        assertEquals(0.05f, careAccumulator.level, 0.001f)
        val last = router.lastAction.value
        assertNotNull(last)
        assertEquals(GestureType.OFFERING, last?.gesture)
        assertEquals(CareAccumulator.CareAction.OFFERING, last?.careAction)
    }

    @Test
    fun sheltering_gesture_appliesSheltering() {
        router.routeGesture(GestureType.SHELTERING)
        assertEquals(0.08f, careAccumulator.level, 0.001f)
    }

    @Test
    fun pinch_gesture_appliesBuilding() {
        router.routeGesture(GestureType.PINCH)
        assertEquals(0.03f, careAccumulator.level, 0.001f)
    }

    @Test
    fun guiding_gesture_appliesGuiding() {
        router.routeGesture(GestureType.GUIDING)
        assertEquals(0.06f, careAccumulator.level, 0.001f)
    }

    @Test
    fun calling_gesture_doesNotMapToCareAction() {
        router.routeGesture(GestureType.CALLING)
        assertEquals(0f, careAccumulator.level, 0.001f)
    }

    @Test
    fun sameGesture_respectsDebounce() {
        router.routeGesture(GestureType.OFFERING)
        router.routeGesture(GestureType.OFFERING)
        assertEquals(0.05f, careAccumulator.level, 0.001f)
        assertEquals(1, router.actionHistory.value.size)
    }

    @Test
    fun differentGestures_afterDebounce_bothApply() {
        router.routeGesture(GestureType.OFFERING)
        currentTimeMs += InputRouter.DEBOUNCE_MS + 1
        router.routeGesture(GestureType.SHELTERING)
        assertEquals(0.13f, careAccumulator.level, 0.001f)
        assertEquals(2, router.actionHistory.value.size)
    }

    @Test
    fun reset_clearsHistory() {
        router.routeGesture(GestureType.OFFERING)
        router.reset()
        assertNull(router.lastAction.value)
        assertTrue(router.actionHistory.value.isEmpty())
    }

    @Test
    fun actionHistory_tracksLastAction() {
        router.routeGesture(GestureType.OFFERING)
        val action = router.lastAction.value
        assertNotNull(action)
        assertEquals(GestureType.OFFERING, action?.gesture)
    }

    private fun assertTrue(value: Boolean) {
        if (!value) throw AssertionError("Expected true")
    }
}
