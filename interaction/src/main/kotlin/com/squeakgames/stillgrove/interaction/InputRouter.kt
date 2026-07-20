package com.squeakgames.stillgrove.interaction

import com.squeakgames.stillgrove.engine.CareAccumulator
import com.squeakgames.stillgrove.interaction.HandGestureRecognizer.GestureType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RoutedAction(
    val gesture: GestureType,
    val careAction: CareAccumulator.CareAction,
    val timestamp: Long,
)

class InputRouter(
    private val careAccumulator: CareAccumulator,
    private val timeMs: () -> Long = { System.currentTimeMillis() },
) {

    private val _lastAction = MutableStateFlow<RoutedAction?>(null)
    val lastAction: StateFlow<RoutedAction?> = _lastAction.asStateFlow()

    private val _actionHistory = MutableStateFlow<List<RoutedAction>>(emptyList())
    val actionHistory: StateFlow<List<RoutedAction>> = _actionHistory.asStateFlow()

    private var lastGestureMs: Long = 0L

    fun routeGesture(gesture: GestureType) {
        val now = timeMs()
        if (now - lastGestureMs < DEBOUNCE_MS) return
        lastGestureMs = now

        val action = mapGesture(gesture) ?: return
        careAccumulator.apply(action)

        val routed = RoutedAction(
            gesture = gesture,
            careAction = action,
            timestamp = now,
        )
        _lastAction.value = routed
        _actionHistory.value = _actionHistory.value + routed
    }

    fun reset() {
        _lastAction.value = null
        _actionHistory.value = emptyList()
    }

    private fun mapGesture(gesture: GestureType): CareAccumulator.CareAction? {
        return when (gesture) {
            GestureType.OFFERING -> CareAccumulator.CareAction.OFFERING
            GestureType.SHELTERING -> CareAccumulator.CareAction.SHELTERING
            GestureType.PINCH -> CareAccumulator.CareAction.BUILDING
            GestureType.GUIDING -> CareAccumulator.CareAction.GUIDING
            GestureType.CALLING -> null
        }
    }

    companion object {
        const val DEBOUNCE_MS = 200L
    }
}
