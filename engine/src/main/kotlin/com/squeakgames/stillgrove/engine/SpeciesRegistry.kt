package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

data class CreatureInstance(
    val id: String = UUID.randomUUID().toString(),
    val species: CreatureSpecies,
    val displayName: String,
    val fsm: CreatureFSM = CreatureFSM(),
    val careAccumulator: CareAccumulator = CareAccumulator(),
)

class SpeciesRegistry {

    private val _creatures = MutableStateFlow<List<CreatureInstance>>(emptyList())
    val creatures: StateFlow<List<CreatureInstance>> = _creatures.asStateFlow()

    fun spawn(species: CreatureSpecies): CreatureInstance {
        val instance = CreatureInstance(
            species = species,
            displayName = species.displayName,
        )
        _creatures.value = _creatures.value + instance
        return instance
    }

    fun spawn(count: Int, species: CreatureSpecies): List<CreatureInstance> {
        val instances = (1..count).map {
            CreatureInstance(species = species, displayName = species.displayName)
        }
        _creatures.value = _creatures.value + instances
        return instances
    }

    fun remove(creatureId: String) {
        _creatures.value = _creatures.value.filter { it.id != creatureId }
    }

    fun get(creatureId: String): CreatureInstance? {
        return _creatures.value.find { it.id == creatureId }
    }

    fun clear() {
        _creatures.value = emptyList()
    }

    fun creatureIds(): List<String> = _creatures.value.map { it.id }

    fun countBySpecies(): Map<CreatureSpecies, Int> {
        return _creatures.value.groupBy { it.species }.mapValues { it.value.size }
    }
}
