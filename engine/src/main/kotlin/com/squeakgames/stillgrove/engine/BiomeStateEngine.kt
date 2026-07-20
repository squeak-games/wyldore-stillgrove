package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BiomeHealth {
    THRIVING,
    STABLE,
    ENTROPIC,
    DEEPLY_ENTROPIC,
}

data class SurfaceAffinity(
    val surfaceId: String,
    val healthContribution: Float,
    val creatureCount: Int,
)

class BiomeStateEngine(
    private val careAccumulator: CareAccumulator,
    private val creatureFsm: CreatureFSM,
) {
    private val _health = MutableStateFlow(BiomeHealth.STABLE)
    val health: StateFlow<BiomeHealth> = _health.asStateFlow()

    private val _surfaceAffinities = MutableStateFlow<List<SurfaceAffinity>>(emptyList())
    val surfaceAffinities: StateFlow<List<SurfaceAffinity>> = _surfaceAffinities.asStateFlow()

    private val _entropyLevel = MutableStateFlow(0f)
    val entropyLevel: StateFlow<Float> = _entropyLevel.asStateFlow()

    fun tick(surfaceCounts: Map<String, Int>, deltaHours: Float = 1f) {
        val baseEntropy = computeBaseEntropy(deltaHours)
        val collectiveResistance = computeCollectiveEntropyResistance(surfaceCounts)
        val entropy = (baseEntropy - collectiveResistance).coerceIn(0f, 1f)

        _entropyLevel.value = entropy
        _health.value = computeHealth(entropy)
        _surfaceAffinities.value = computeAffinities(surfaceCounts, entropy)
    }

    fun getSurfaceHealth(surfaceId: String): Float {
        val affinity = _surfaceAffinities.value.find { it.surfaceId == surfaceId }
        return affinity?.healthContribution ?: 0.5f
    }

    fun reset() {
        _health.value = BiomeHealth.STABLE
        _surfaceAffinities.value = emptyList()
        _entropyLevel.value = 0f
    }

    private fun computeBaseEntropy(deltaHours: Float): Float {
        val care = careAccumulator.level
        val stateMultiplier = when (creatureFsm.state) {
            CreatureState.THRIVING -> 0.3f
            CreatureState.STABLE -> 0.5f
            CreatureState.ENTROPIC -> 0.8f
            CreatureState.DORMANT -> 1.0f
        }
        val careBoost = 1f - care
        return (ENTROPY_BASE_RATE * deltaHours * stateMultiplier * careBoost).coerceIn(0f, 1f)
    }

    private fun computeCollectiveEntropyResistance(surfaceCounts: Map<String, Int>): Float {
        if (surfaceCounts.isEmpty()) return 0f
        val totalCreatures = surfaceCounts.values.sum()
        return totalCreatures * ENTROPY_RESISTANCE_PER_CREATURE
    }

    private fun computeHealth(entropy: Float): BiomeHealth {
        return when {
            entropy < 0.25f -> BiomeHealth.THRIVING
            entropy < 0.50f -> BiomeHealth.STABLE
            entropy < 0.75f -> BiomeHealth.ENTROPIC
            else -> BiomeHealth.DEEPLY_ENTROPIC
        }
    }

    private fun computeAffinities(
        surfaceCounts: Map<String, Int>,
        entropy: Float,
    ): List<SurfaceAffinity> {
        return surfaceCounts.map { (surfaceId, count) ->
            val healthFactor = 1f - entropy
            val densityFactor = (count.toFloat() / (surfaceCounts.values.maxOrNull() ?: 1)).coerceIn(0f, 1f)
            SurfaceAffinity(
                surfaceId = surfaceId,
                healthContribution = (healthFactor + densityFactor) / 2f,
                creatureCount = count,
            )
        }.sortedByDescending { it.healthContribution }
    }

    companion object {
        const val ENTROPY_BASE_RATE = 0.05f
        const val ENTROPY_RESISTANCE_PER_CREATURE = 0.02f
    }
}
