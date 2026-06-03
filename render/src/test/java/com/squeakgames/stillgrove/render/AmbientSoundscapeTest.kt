package com.squeakgames.stillgrove.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AmbientSoundscapeTest {

    @Test
    fun startsStable() {
        val scape = AmbientSoundscape()
        assertEquals(BiomeHealth.STABLE, scape.currentBiome.value)
    }

    @Test
    fun update_changesBiome() {
        val scape = AmbientSoundscape()
        scape.update(BiomeHealth.THRIVING)
        assertEquals(BiomeHealth.THRIVING, scape.currentBiome.value)
    }

    @Test
    fun soundLayers_generatedForBiome() {
        val scape = AmbientSoundscape()
        scape.update(BiomeHealth.THRIVING)
        assertTrue(scape.activeLayers.value.isNotEmpty())
    }

    @Test
    fun thriving_higherDensityThanEntropic() {
        val scape = AmbientSoundscape()
        scape.update(BiomeHealth.THRIVING)
        val thrivingDensity = scape.activeLayers.value.first().amplitude
        scape.update(BiomeHealth.DEEPLY_ENTROPIC)
        val entropicDensity = scape.activeLayers.value.first().amplitude
        assertTrue(thrivingDensity > entropicDensity)
    }

    @Test
    fun reset_restoresStable() {
        val scape = AmbientSoundscape()
        scape.update(BiomeHealth.THRIVING)
        scape.reset()
        assertEquals(BiomeHealth.STABLE, scape.currentBiome.value)
    }

    @Test
    fun biomeHealth_hasExpectedValues() {
        assertEquals(220f, BiomeHealth.THRIVING.baseFrequency, 0.001f)
        assertEquals(60f, BiomeHealth.DEEPLY_ENTROPIC.baseFrequency, 0.001f)
    }
}
