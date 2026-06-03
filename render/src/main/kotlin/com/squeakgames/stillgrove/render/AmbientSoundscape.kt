package com.squeakgames.stillgrove.render

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class BiomeHealth(val label: String, val baseFrequency: Float, val density: Float) {
    THRIVING("Thriving", 220f, 0.9f),
    STABLE("Stable", 180f, 0.6f),
    ENTROPIC("Entropic", 120f, 0.3f),
    DEEPLY_ENTROPIC("Deeply Entropic", 60f, 0.1f),
}

class AmbientSoundscape {

    private val _currentBiome = MutableStateFlow(BiomeHealth.STABLE)
    val currentBiome: StateFlow<BiomeHealth> = _currentBiome.asStateFlow()

    private val _activeLayers = MutableStateFlow<List<SoundLayer>>(emptyList())
    val activeLayers: StateFlow<List<SoundLayer>> = _activeLayers.asStateFlow()

    fun update(biomeHealth: BiomeHealth) {
        _currentBiome.value = biomeHealth
        _activeLayers.value = generateLayers(biomeHealth)
    }

    fun reset() {
        _currentBiome.value = BiomeHealth.STABLE
        _activeLayers.value = emptyList()
    }

    private fun generateLayers(health: BiomeHealth): List<SoundLayer> {
        return listOf(
            SoundLayer(
                name = "ambient_drone",
                frequency = health.baseFrequency,
                amplitude = health.density,
                harmonics = listOf(1f, 0.5f, 0.25f),
            ),
            SoundLayer(
                name = "creature_chorus",
                frequency = health.baseFrequency * 2f,
                amplitude = health.density * 0.6f,
                harmonics = listOf(1f, 0.3f),
            ),
        )
    }

    data class SoundLayer(
        val name: String,
        val frequency: Float,
        val amplitude: Float,
        val harmonics: List<Float>,
    )
}
