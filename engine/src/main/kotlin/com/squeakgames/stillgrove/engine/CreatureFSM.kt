package com.squeakgames.stillgrove.engine

class CreatureFSM {

    var state: CreatureState = CreatureState.STABLE
        private set

    fun advance(careLevel: Float, hoursSinceLastTending: Float) {
        state = when {
            careLevel >= 0.75f && hoursSinceLastTending < 2f -> CreatureState.THRIVING
            careLevel >= 0.30f || hoursSinceLastTending < 6f -> CreatureState.STABLE
            hoursSinceLastTending < 12f -> CreatureState.ENTROPIC
            else -> CreatureState.DORMANT
        }
    }

    fun reset() {
        state = CreatureState.STABLE
    }
}
