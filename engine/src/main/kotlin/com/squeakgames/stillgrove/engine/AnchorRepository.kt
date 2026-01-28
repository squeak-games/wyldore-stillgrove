package com.squeakgames.stillgrove.engine

import android.content.Context
import android.content.SharedPreferences
import androidx.xr.scenecore.AnchorEntity
import androidx.xr.scenecore.Dimensions
import androidx.xr.scenecore.PlaneSemantic
import androidx.xr.scenecore.PlaneType
import androidx.xr.scenecore.Session
import java.util.UUID

class AnchorRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val persistedAnchors: MutableList<UUID> = loadUuids()
    private val activeAnchors: MutableList<AnchorEntity> = mutableListOf()

    fun createAndPersist(session: Session): UUID? {
        val entity = AnchorEntity.create(
            session,
            Dimensions(0.3f, 0.3f, 0.3f),
            PlaneType.HORIZONTAL,
            PlaneSemantic.ANY,
        )
        val uuid = entity.persist() ?: return null
        activeAnchors.add(entity)
        persistedAnchors.add(uuid)
        saveUuids()
        return uuid
    }

    fun activeAnchors(): List<AnchorEntity> = activeAnchors.toList()

    fun persistedAnchorUuids(): List<UUID> = persistedAnchors.toList()

    fun clear() {
        activeAnchors.clear()
        persistedAnchors.clear()
        prefs.edit().clear().apply()
    }

    private fun loadUuids(): MutableList<UUID> {
        val count = prefs.getInt(PREF_ANCHOR_COUNT, 0)
        val list = mutableListOf<UUID>()
        for (i in 0 until count) {
            val uuidStr = prefs.getString("$PREF_ANCHOR_PREFIX$i", null)
            if (uuidStr != null) {
                try {
                    list.add(UUID.fromString(uuidStr))
                } catch (_: IllegalArgumentException) { }
            }
        }
        return list
    }

    private fun saveUuids() {
        prefs.edit().putInt(PREF_ANCHOR_COUNT, persistedAnchors.size).apply()
        persistedAnchors.forEachIndexed { i, uuid ->
            prefs.edit().putString("$PREF_ANCHOR_PREFIX$i", uuid.toString()).apply()
        }
    }

    companion object {
        private const val PREFS_NAME = "anchor_repository"
        private const val PREF_ANCHOR_COUNT = "anchor_count"
        private const val PREF_ANCHOR_PREFIX = "anchor_"
    }
}
