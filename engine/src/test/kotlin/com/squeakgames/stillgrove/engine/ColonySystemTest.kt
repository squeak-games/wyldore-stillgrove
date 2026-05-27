package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ColonySystemTest {

    @Test
    fun startsEmpty() {
        val cs = ColonySystem(BondGraph())
        assertTrue(cs.colonies.value.isEmpty())
    }

    @Test
    fun threeBondedCreatures_formColony() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        bg.updateProximity("b", "c", 180f)
        val cs = ColonySystem(bg)
        cs.tick(listOf("a", "b", "c"))
        assertEquals(1, cs.colonies.value.size)
        assertEquals(3, cs.colonies.value.first().size)
    }

    @Test
    fun twoBondedCreatures_noColony() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        val cs = ColonySystem(bg)
        cs.tick(listOf("a", "b"))
        assertTrue(cs.colonies.value.isEmpty())
    }

    @Test
    fun unaffiliatedCreature_notInColony() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        bg.updateProximity("b", "c", 180f)
        val cs = ColonySystem(bg)
        cs.tick(listOf("a", "b", "c", "d"))
        assertNull(cs.colonyFor("d"))
    }

    @Test
    fun colony_entropyResistance_scalesWithSize() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        bg.updateProximity("b", "c", 180f)
        bg.updateProximity("c", "d", 180f)
        val cs = ColonySystem(bg)
        cs.tick(listOf("a", "b", "c", "d"))
        assertEquals(0.4f, cs.colonies.value.first().entropyResistance, 0.001f)
    }

    @Test
    fun clear_removesAll() {
        val bg = BondGraph()
        bg.updateProximity("a", "b", 180f)
        bg.updateProximity("b", "c", 180f)
        val cs = ColonySystem(bg)
        cs.tick(listOf("a", "b", "c"))
        cs.clear()
        assertTrue(cs.colonies.value.isEmpty())
    }
}
