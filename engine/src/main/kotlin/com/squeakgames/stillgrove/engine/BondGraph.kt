package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BondGraph {

    private val _bonds = MutableStateFlow<List<Bond>>(emptyList())
    val bonds: StateFlow<List<Bond>> = _bonds.asStateFlow()

    private val _pendingPairs = mutableSetOf<Pair<String, String>>()
    private val proximityTimers = mutableMapOf<Pair<String, String>, Float>()

    fun updateProximity(creatureIdA: String, creatureIdB: String, seconds: Float) {
        val key = normalizedKey(creatureIdA, creatureIdB)
        val current = proximityTimers.getOrDefault(key, 0f)
        proximityTimers[key] = current + seconds

        if (proximityTimers[key]!! >= RECOGNITION_TIME_SEC) {
            _pendingPairs.add(key)
        }

        if (proximityTimers[key]!! >= BOND_FORMATION_TIME_SEC &&
            _pendingPairs.contains(key) &&
            !isBonded(creatureIdA, creatureIdB)
        ) {
            formBond(creatureIdA, creatureIdB)
        }
    }

    fun areInProximity(creatureIdA: String, creatureIdB: String): Boolean {
        val key = normalizedKey(creatureIdA, creatureIdB)
        return proximityTimers.getOrDefault(key, 0f) >= RECOGNITION_TIME_SEC
    }

    fun isBonded(creatureIdA: String, creatureIdB: String): Boolean {
        return _bonds.value.any { bond ->
            (bond.creatureIdA == creatureIdA && bond.creatureIdB == creatureIdB) ||
            (bond.creatureIdA == creatureIdB && bond.creatureIdB == creatureIdA)
        }
    }

    fun bondsFor(creatureId: String): List<Bond> {
        return _bonds.value.filter {
            it.creatureIdA == creatureId || it.creatureIdB == creatureId
        }
    }

    fun bondedCreatureIds(creatureId: String): List<String> {
        return bondsFor(creatureId).map { bond ->
            if (bond.creatureIdA == creatureId) bond.creatureIdB else bond.creatureIdA
        }
    }

    fun clear() {
        _bonds.value = emptyList()
        _pendingPairs.clear()
        proximityTimers.clear()
    }

    private fun formBond(creatureIdA: String, creatureIdB: String) {
        val bond = Bond(
            id = UUID.randomUUID().toString(),
            creatureIdA = creatureIdA,
            creatureIdB = creatureIdB,
            formationTimestamp = System.currentTimeMillis(),
            strength = INITIAL_BOND_STRENGTH,
        )
        _bonds.value = _bonds.value + bond
    }

    private fun normalizedKey(a: String, b: String): Pair<String, String> {
        return if (a < b) Pair(a, b) else Pair(b, a)
    }

    data class Bond(
        val id: String,
        val creatureIdA: String,
        val creatureIdB: String,
        val formationTimestamp: Long,
        val strength: Float,
    )

    companion object {
        const val RECOGNITION_TIME_SEC = 60f
        const val BOND_FORMATION_TIME_SEC = 120f
        const val INITIAL_BOND_STRENGTH = 0.3f
    }
}
