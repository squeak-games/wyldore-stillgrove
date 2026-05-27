package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ColonySystem(private val bondGraph: BondGraph) {

    private val _colonies = MutableStateFlow<List<Colony>>(emptyList())
    val colonies: StateFlow<List<Colony>> = _colonies.asStateFlow()

    fun tick(creatureIds: List<String>) {
        val colonyMap = mutableMapOf<String, MutableSet<String>>()
        val assigned = mutableSetOf<String>()

        for (creatureId in creatureIds) {
            if (creatureId in assigned) continue
            val bonded = bondGraph.bondedCreatureIds(creatureId).toMutableSet()
            val cluster = mutableSetOf(creatureId)

            var changed = true
            while (changed) {
                changed = false
                val toAdd = bonded.filter { it !in cluster && it in creatureIds }
                if (toAdd.isNotEmpty()) {
                    cluster.addAll(toAdd)
                    toAdd.forEach { bonded.addAll(bondGraph.bondedCreatureIds(it)) }
                    changed = true
                }
            }

            assigned.addAll(cluster)
            if (cluster.size >= COLONY_MIN_SIZE) {
                colonyMap[cluster.sorted().joinToString("-")] = cluster
            }
        }

        _colonies.value = colonyMap.map { (id, members) ->
            Colony(
                id = id,
                memberIds = members.toList(),
                size = members.size,
                entropyResistance = members.size * 0.1f,
            )
        }
    }

    fun colonyFor(creatureId: String): Colony? {
        return _colonies.value.find { creatureId in it.memberIds }
    }

    fun clear() {
        _colonies.value = emptyList()
    }

    data class Colony(
        val id: String,
        val memberIds: List<String>,
        val size: Int,
        val entropyResistance: Float,
    )

    companion object {
        const val COLONY_MIN_SIZE = 3
    }
}
