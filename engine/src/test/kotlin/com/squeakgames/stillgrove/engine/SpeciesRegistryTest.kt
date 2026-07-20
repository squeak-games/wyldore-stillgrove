package com.squeakgames.stillgrove.engine

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SpeciesRegistryTest {

    private lateinit var registry: SpeciesRegistry

    @Before
    fun setUp() {
        registry = SpeciesRegistry()
    }

    @Test
    fun startsEmpty() {
        assertTrue(registry.creatures.value.isEmpty())
        assertTrue(registry.creatureIds().isEmpty())
    }

    @Test
    fun spawn_addsCreature() {
        val creature = registry.spawn(CreatureSpecies.EMBER_MOTE)
        assertEquals(1, registry.creatures.value.size)
        assertEquals(CreatureSpecies.EMBER_MOTE, creature.species)
        assertNotNull(creature.id)
    }

    @Test
    fun spawnMultiple_addsCorrectCount() {
        val creatures = registry.spawn(3, CreatureSpecies.THREADWEFT)
        assertEquals(3, creatures.size)
        assertEquals(3, registry.creatures.value.size)
        creatures.forEach { assertEquals(CreatureSpecies.THREADWEFT, it.species) }
    }

    @Test
    fun get_returnsCreatureById() {
        val creature = registry.spawn(CreatureSpecies.HUSHLING)
        val found = registry.get(creature.id)
        assertNotNull(found)
        assertEquals(creature.id, found?.id)
    }

    @Test
    fun get_returnsNullForUnknownId() {
        val result = registry.get("nonexistent")
        assertNull(result)
    }

    @Test
    fun remove_deletesCreature() {
        val creature = registry.spawn(CreatureSpecies.DRIFTSPORE)
        registry.remove(creature.id)
        assertTrue(registry.creatures.value.isEmpty())
    }

    @Test
    fun countBySpecies_returnsCorrectTallies() {
        registry.spawn(CreatureSpecies.EMBER_MOTE)
        registry.spawn(2, CreatureSpecies.ROOTCLASP)
        val counts = registry.countBySpecies()
        assertEquals(1, counts[CreatureSpecies.EMBER_MOTE])
        assertEquals(2, counts[CreatureSpecies.ROOTCLASP])
    }

    @Test
    fun clear_removesAll() {
        registry.spawn(CreatureSpecies.EMBER_MOTE)
        registry.spawn(CreatureSpecies.THREADWEFT)
        registry.clear()
        assertTrue(registry.creatures.value.isEmpty())
    }

    @Test
    fun creatureIds_returnsAllIds() {
        val c1 = registry.spawn(CreatureSpecies.EMBER_MOTE)
        val c2 = registry.spawn(CreatureSpecies.THREADWEFT)
        val ids = registry.creatureIds()
        assertTrue(ids.contains(c1.id))
        assertTrue(ids.contains(c2.id))
    }
}
