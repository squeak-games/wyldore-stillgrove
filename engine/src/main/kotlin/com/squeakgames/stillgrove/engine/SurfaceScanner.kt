package com.squeakgames.stillgrove.engine

import androidx.xr.scenecore.AnchorEntity
import androidx.xr.scenecore.Dimensions
import androidx.xr.scenecore.PlaneSemantic
import androidx.xr.scenecore.PlaneType
import androidx.xr.scenecore.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SurfaceScanner(private val session: Session) {

    private val _detectedPlanes = MutableStateFlow<List<AnchorEntity>>(emptyList())
    val detectedPlanes: StateFlow<List<AnchorEntity>> = _detectedPlanes.asStateFlow()

    fun startScanning() {
        val entity = AnchorEntity.create(
            session,
            Dimensions(1.0f, 0.01f, 1.0f),
            PlaneType.HORIZONTAL,
            PlaneSemantic.ANY,
        )
        _detectedPlanes.value = listOf(entity)
    }

    fun stopScanning() {
        _detectedPlanes.value = emptyList()
    }
}
