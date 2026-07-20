package com.squeakgames.stillgrove.engine

enum class CreatureSpecies(
    val displayName: String,
    val baseEntropyResistance: Float,
    val bondAffinity: Float,
    val vocalRangeMin: Float,
    val vocalRangeMax: Float,
) {
    EMBER_MOTE(
        displayName = "Ember Mote",
        baseEntropyResistance = 0.4f,
        bondAffinity = 0.3f,
        vocalRangeMin = 200f,
        vocalRangeMax = 400f,
    ),
    THREADWEFT(
        displayName = "Threadweft",
        baseEntropyResistance = 0.3f,
        bondAffinity = 0.6f,
        vocalRangeMin = 300f,
        vocalRangeMax = 600f,
    ),
    HUSHLING(
        displayName = "Hushling",
        baseEntropyResistance = 0.5f,
        bondAffinity = 0.4f,
        vocalRangeMin = 150f,
        vocalRangeMax = 350f,
    ),
    DRIFTSPORE(
        displayName = "Driftspore",
        baseEntropyResistance = 0.2f,
        bondAffinity = 0.5f,
        vocalRangeMin = 400f,
        vocalRangeMax = 800f,
    ),
    ROOTCLASP(
        displayName = "Rootclasp",
        baseEntropyResistance = 0.6f,
        bondAffinity = 0.2f,
        vocalRangeMin = 100f,
        vocalRangeMax = 250f,
    ),
}
