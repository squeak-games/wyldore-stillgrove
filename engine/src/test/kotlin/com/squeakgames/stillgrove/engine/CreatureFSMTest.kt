package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Test

class CreatureFSMTest {

    @Test
    fun startsStable() {
        val fsm = CreatureFSM()
        assertEquals(CreatureState.STABLE, fsm.state)
    }

    @Test
    fun highCare_andRecentTending_transitionsToThriving() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.80f, hoursSinceLastTending = 1f)
        assertEquals(CreatureState.THRIVING, fsm.state)
    }

    @Test
    fun moderateCare_transitionsToStable() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.50f, hoursSinceLastTending = 3f)
        assertEquals(CreatureState.STABLE, fsm.state)
    }

    @Test
    fun lowCare_andLongAbsence_transitionsToEntropic() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.10f, hoursSinceLastTending = 8f)
        assertEquals(CreatureState.ENTROPIC, fsm.state)
    }

    @Test
    fun veryLowCare_andVeryLongAbsence_transitionsToDormant() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.0f, hoursSinceLastTending = 24f)
        assertEquals(CreatureState.DORMANT, fsm.state)
    }

    @Test
    fun highCareWithLongAbsence_transitionsToStable() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.80f, hoursSinceLastTending = 10f)
        assertEquals(CreatureState.STABLE, fsm.state)
    }

    @Test
    fun reset_returnsToStable() {
        val fsm = CreatureFSM()
        fsm.advance(careLevel = 0.0f, hoursSinceLastTending = 24f)
        assertEquals(CreatureState.DORMANT, fsm.state)
        fsm.reset()
        assertEquals(CreatureState.STABLE, fsm.state)
    }
}
