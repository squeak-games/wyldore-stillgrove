package com.squeakgames.stillgrove.render

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SceneCoreRenderLayerTest {

    @Test
    fun biomeHealth_hasFourStates() {
        val states = BiomeHealth.values()
        assertEquals(4, states.size)
    }

    @Test
    fun biomeHealth_transitionsAreOrdered() {
        val ordered = listOf(
            BiomeHealth.THRIVING,
            BiomeHealth.STABLE,
            BiomeHealth.ENTROPIC,
            BiomeHealth.DEEPLY_ENTROPIC,
        )
        assertEquals(ordered, BiomeHealth.values().toList())
    }

    @Test
    fun sceneNodeTypes_areDefined() {
        assertEquals(5, NodeType.values().size)
    }

    @Test
    fun proceduralArt_defaultsToEmpty() {
        val art = ProceduralBiomeArt()
        assertTrue(art.mossPatches.isEmpty())
        assertTrue(art.rootTendrils.isEmpty())
        assertTrue(art.particleEmitters.isEmpty())
    }
}
