package com.squeakgames.stillgrove.engine

class CreatureAIManager {
    val fsm = CreatureFSM()

    fun tick(careLevel: Float, hoursSinceLastTending: Float) {
        fsm.advance(careLevel, hoursSinceLastTending)
    }

    fun currentState(): CreatureState = fsm.state
}
