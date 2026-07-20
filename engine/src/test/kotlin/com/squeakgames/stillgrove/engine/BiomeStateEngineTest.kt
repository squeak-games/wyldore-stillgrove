package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class BiomeStateEngineTest {

    private lateinit var careAccumulator: CareAccumulator
    private lateinit var creatureFsm: CreatureFSM
    private lateinit var engine: BiomeStateEngine

    @Before
    fun setUp() {
        careAccumulator = CareAccumulator()
        creatureFsm = CreatureFSM()
        engine = BiomeStateEngine(careAccumulator, creatureFsm)
    }

    @Test
    fun startsAtStableHealth() {
        assertEquals(BiomeHealth.STABLE, engine.health.value)
    }

    @Test
    fun entropyStartsAtZero() {
        assertEquals(0f, engine.entropyLevel.value, 0.001f)
    }

    @Test
    fun highCare_reducesEntropy() {
        careAccumulator.apply(CareAccumulator.CareAction.OFFERING)
        careAccumulator.apply(CareAccumulator.CareAction.SHELTERING)
        careAccumulator.apply(CareAccumulator.CareAction.WITNESSING_BOND)
        creatureFsm.advance(careLevel = 0.25f, hoursSinceLastTending = 1f)

        engine.tick(surfaceCounts = mapOf("surface_1" to 2), deltaHours = 4f)

        assertTrue(engine.entropyLevel.value < 0.5f)
    }

    @Test
    fun surfaceAffinity_reflectsCreatureDensity() {
        careAccumulator.apply(CareAccumulator.CareAction.OFFERING)
        creatureFsm.advance(careLevel = 0.05f, hoursSinceLastTending = 1f)

        engine.tick(
            surfaceCounts = mapOf(
                "surface_1" to 5,
                "surface_2" to 1,
            ),
            deltaHours = 2f,
        )

        val affinities = engine.surfaceAffinities.value
        assertEquals(2, affinities.size)
        assertTrue(affinities.first().healthContribution >= affinities.last().healthContribution)
    }

    @Test
    fun manyCreatures_lowerEntropyThroughCollectiveResistance() {
        creatureFsm.advance(careLevel = 0f, hoursSinceLastTending = 4f)

        engine.tick(surfaceCounts = mapOf("surface_1" to 10), deltaHours = 4f)

        assertTrue(engine.entropyLevel.value <= 0.5f)
    }

    @Test
    fun noCreatures_entropyIncreases() {
        creatureFsm.advance(careLevel = 0f, hoursSinceLastTending = 12f)

        engine.tick(surfaceCounts = emptyMap(), deltaHours = 12f)

        assertTrue(engine.entropyLevel.value > 0f)
    }

    @Test
    fun deepEntropy_whenCareIsZeroAndTimePasses() {
        creatureFsm.advance(careLevel = 0f, hoursSinceLastTending = 48f)

        engine.tick(surfaceCounts = emptyMap(), deltaHours = 48f)

        assertEquals(BiomeHealth.DEEPLY_ENTROPIC, engine.health.value)
    }

    @Test
    fun reset_restoresDefaults() {
        careAccumulator.apply(CareAccumulator.CareAction.OFFERING)
        creatureFsm.advance(careLevel = 0.05f, hoursSinceLastTending = 1f)
        engine.tick(surfaceCounts = mapOf("s1" to 1), deltaHours = 4f)

        engine.reset()

        assertEquals(BiomeHealth.STABLE, engine.health.value)
        assertEquals(0f, engine.entropyLevel.value, 0.001f)
        assertTrue(engine.surfaceAffinities.value.isEmpty())
    }
}
