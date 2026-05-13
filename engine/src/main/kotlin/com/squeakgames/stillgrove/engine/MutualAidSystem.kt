package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MutualAidSystem(private val bondGraph: BondGraph) {

    private val _activeAids = MutableStateFlow<List<MutualAid>>(emptyList())
    val activeAids: StateFlow<List<MutualAid>> = _activeAids.asStateFlow()

    fun tick(creaturePositions: Map<String, Position>, deltaMinutes: Float) {
        val aids = mutableListOf<MutualAid>()

        for ((creatureId, pos) in creaturePositions) {
            val partners = bondGraph.bondedCreatureIds(creatureId)

            for (partnerId in partners) {
                val partnerPos = creaturePositions[partnerId] ?: continue
                val distance = distanceBetween(pos, partnerPos)

                if (distance < SHARED_WARMTH_RANGE) {
                    aids.add(MutualAid(
                        creatureIdA = creatureId,
                        creatureIdB = partnerId,
                        type = AidType.SHARED_WARMTH,
                        strength = 0.4f,
                        remainingSeconds = deltaMinutes * 60f,
                    ))
                }

                if (distance < SHELTER_REINFORCEMENT_RANGE) {
                    aids.add(MutualAid(
                        creatureIdA = creatureId,
                        creatureIdB = partnerId,
                        type = AidType.SHELTER_REINFORCEMENT,
                        strength = 0.25f,
                        remainingSeconds = deltaMinutes * 60f,
                    ))
                }
            }

            for (partnerId in partners) {
                if (bondGraph.isBonded(creatureId, partnerId)) {
                    val distressed = creaturePositions.any { (id, _) ->
                        id != creatureId && id != partnerId &&
                        bondGraph.isBonded(creatureId, id)
                    }
                    if (distressed) {
                        aids.add(MutualAid(
                            creatureIdA = creatureId,
                            creatureIdB = partnerId,
                            type = AidType.DISTRESS_SIGNALING,
                            strength = 0.8f,
                            remainingSeconds = 30f,
                        ))
                    }
                }
            }
        }

        _activeAids.value = aids
    }

    fun clear() {
        _activeAids.value = emptyList()
    }

    enum class AidType {
        SHARED_WARMTH,
        SHELTER_REINFORCEMENT,
        DISTRESS_SIGNALING,
        COMFORT_TENDING,
    }

    data class MutualAid(
        val creatureIdA: String,
        val creatureIdB: String,
        val type: AidType,
        val strength: Float,
        val remainingSeconds: Float,
    )

    data class Position(val x: Float, val y: Float, val z: Float)

    companion object {
        const val SHARED_WARMTH_RANGE = 0.5f
        const val SHELTER_REINFORCEMENT_RANGE = 1.0f
    }
}

private fun distanceBetween(a: MutualAidSystem.Position, b: MutualAidSystem.Position): Float {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return kotlin.math.sqrt(dx * dx + dy * dy + dz * dz)
}
