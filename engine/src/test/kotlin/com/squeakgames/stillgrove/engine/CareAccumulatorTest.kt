package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CareAccumulatorTest {

    @Test
    fun startsAtZero() {
        val acc = CareAccumulator()
        assertEquals(0f, acc.level, 0.001f)
    }

    @Test
    fun offering_increasesBy5Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.OFFERING)
        assertEquals(0.05f, acc.level, 0.001f)
    }

    @Test
    fun sheltering_increasesBy8Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.SHELTERING)
        assertEquals(0.08f, acc.level, 0.001f)
    }

    @Test
    fun building_increasesBy3Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.BUILDING)
        assertEquals(0.03f, acc.level, 0.001f)
    }

    @Test
    fun guiding_increasesBy6Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.GUIDING)
        assertEquals(0.06f, acc.level, 0.001f)
    }

    @Test
    fun witnessingBond_increasesBy12Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.WITNESSING_BOND)
        assertEquals(0.12f, acc.level, 0.001f)
    }

    @Test
    fun observingMutualAid_increasesBy4Percent() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.OBSERVING_MUTUAL_AID)
        assertEquals(0.04f, acc.level, 0.001f)
    }

    @Test
    fun multipleActions_accumulate() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.OFFERING)
        acc.apply(CareAccumulator.CareAction.SHELTERING)
        assertEquals(0.13f, acc.level, 0.001f)
    }

    @Test
    fun idleDecay_reducesLevel() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.OFFERING)
        acc.tickIdle(1f)
        assertEquals(0.03f, acc.level, 0.001f)
    }

    @Test
    fun idleDecay_doesNotGoBelowZero() {
        val acc = CareAccumulator()
        acc.tickIdle(60f)
        assertEquals(0f, acc.level, 0.001f)
    }

    @Test
    fun level_capsAtOne() {
        val acc = CareAccumulator()
        repeat(20) { acc.apply(CareAccumulator.CareAction.OFFERING) }
        assertEquals(1f, acc.level, 0.001f)
    }

    @Test
    fun bloomThreshold_above85Percent() {
        val acc = CareAccumulator()
        assertFalse(acc.isBlooming)
        repeat(17) { acc.apply(CareAccumulator.CareAction.OFFERING) }
        assertTrue(acc.isBlooming)
    }

    @Test
    fun reset_returnsToZero() {
        val acc = CareAccumulator()
        acc.apply(CareAccumulator.CareAction.SHELTERING)
        assertEquals(0.08f, acc.level, 0.001f)
        acc.reset()
        assertEquals(0f, acc.level, 0.001f)
    }
}
