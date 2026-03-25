package com.squeakgames.stillgrove.engine

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DimmingController(
    private val timeMs: () -> Long = { java.lang.System.currentTimeMillis() },
) {

    private val _opacity = MutableStateFlow(INITIAL_OPACITY)
    val opacity: StateFlow<Float> = _opacity.asStateFlow()

    private var lastUpdateMs: Long = timeMs()
    private var displayedOpacity: Float = INITIAL_OPACITY

    fun update(careLevel: Float) {
        val now = timeMs()
        val deltaMs = (now - lastUpdateMs).coerceAtMost(1000L)
        lastUpdateMs = now

        val targetOpacity = computeTargetOpacity(careLevel)
        val maxDelta = MAX_CHANGE_PER_SECOND * deltaMs / 1000f

        displayedOpacity = when {
            targetOpacity > displayedOpacity -> {
                (displayedOpacity + maxDelta).coerceAtMost(targetOpacity)
            }
            targetOpacity < displayedOpacity -> {
                (displayedOpacity - maxDelta).coerceAtLeast(targetOpacity)
            }
            else -> displayedOpacity
        }

        _opacity.value = displayedOpacity
    }

    fun reset() {
        displayedOpacity = INITIAL_OPACITY
        _opacity.value = INITIAL_OPACITY
        lastUpdateMs = timeMs()
    }

    private fun computeTargetOpacity(careLevel: Float): Float {
        val clamped = careLevel.coerceIn(0f, 1f)
        return 1.0f - (clamped * 0.9f)
    }

    companion object {
        const val INITIAL_OPACITY = 1.0f
        const val MAX_CHANGE_PER_SECOND = 0.01f
    }
}
