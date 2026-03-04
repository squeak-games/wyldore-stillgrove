package com.squeakgames.stillgrove.engine

class CareAccumulator {

    var level: Float = 0f
        private set

    val isBlooming: Boolean get() = level >= BLOOM_THRESHOLD

    fun apply(action: CareAction) {
        level = (level + action.weight).coerceIn(0f, 1f)
    }

    fun tickIdle(minutes: Float) {
        val decay = IDLE_DECAY_PER_MINUTE * minutes
        level = (level - decay).coerceIn(0f, 1f)
    }

    fun reset() {
        level = 0f
    }

    enum class CareAction(val weight: Float) {
        OFFERING(0.05f),
        SHELTERING(0.08f),
        BUILDING(0.03f),
        GUIDING(0.06f),
        WITNESSING_BOND(0.12f),
        OBSERVING_MUTUAL_AID(0.04f),
    }

    companion object {
        const val IDLE_DECAY_PER_MINUTE = 0.02f
        const val BLOOM_THRESHOLD = 0.85f
    }
}
