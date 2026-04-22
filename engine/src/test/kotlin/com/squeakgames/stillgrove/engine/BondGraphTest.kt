package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class BondGraphTest {

    @Test
    fun startsEmpty() {
        val graph = BondGraph()
        assertTrue(graph.bonds.value.isEmpty())
    }

    @Test
    fun proximity_belowRecognition_notInProximity() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 30f)
        assertFalse(graph.areInProximity("a", "b"))
    }

    @Test
    fun proximity_aboveRecognition_inProximity() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 90f)
        assertTrue(graph.areInProximity("a", "b"))
    }

    @Test
    fun bondForms_afterSufficientProximity() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        assertTrue(graph.isBonded("a", "b"))
    }

    @Test
    fun bond_notFormed_belowThreshold() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 90f)
        assertFalse(graph.isBonded("a", "b"))
    }

    @Test
    fun bond_isDirectional() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        assertTrue(graph.isBonded("a", "b"))
        assertTrue(graph.isBonded("b", "a"))
    }

    @Test
    fun bondsFor_returnsCorrectBonds() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        val bonds = graph.bondsFor("a")
        assertEquals(1, bonds.size)
    }

    @Test
    fun bondedCreatureIds_returnsPartner() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        val partners = graph.bondedCreatureIds("a")
        assertEquals(listOf("b"), partners)
    }

    @Test
    fun clear_removesAll() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        assertTrue(graph.isBonded("a", "b"))
        graph.clear()
        assertFalse(graph.isBonded("a", "b"))
        assertTrue(graph.bonds.value.isEmpty())
    }

    @Test
    fun multipleBonds_formIndependently() {
        val graph = BondGraph()
        graph.updateProximity("a", "b", 180f)
        graph.updateProximity("c", "d", 180f)
        assertEquals(2, graph.bonds.value.size)
    }
}
