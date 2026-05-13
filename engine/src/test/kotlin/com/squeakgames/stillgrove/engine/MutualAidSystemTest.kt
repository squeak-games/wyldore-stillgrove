package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MutualAidSystemTest {

    @Test
    fun startsEmpty() {
        val system = MutualAidSystem(BondGraph())
        assertTrue(system.activeAids.value.isEmpty())
    }

    @Test
    fun bondedPair_nearby_sharedWarmth() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        val system = MutualAidSystem(bg)
        system.tick(mapOf(
            "a" to MutualAidSystem.Position(0f,0f,0f),
            "b" to MutualAidSystem.Position(0.3f,0f,0f),
        ), 1f)
        assertTrue(system.activeAids.value.any { it.type == MutualAidSystem.AidType.SHARED_WARMTH })
    }

    @Test
    fun bondedPair_farApart_noAid() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        val system = MutualAidSystem(bg)
        system.tick(mapOf(
            "a" to MutualAidSystem.Position(0f,0f,0f),
            "b" to MutualAidSystem.Position(5f,0f,0f),
        ), 1f)
        assertTrue(system.activeAids.value.isEmpty())
    }

    @Test
    fun clear_removesAll() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        val system = MutualAidSystem(bg)
        system.tick(mapOf(
            "a" to MutualAidSystem.Position(0f,0f,0f),
            "b" to MutualAidSystem.Position(0.3f,0f,0f),
        ), 1f)
        system.clear()
        assertTrue(system.activeAids.value.isEmpty())
    }
}
