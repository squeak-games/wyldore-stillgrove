package com.squeakgames.stillgrove.render

import android.net.Uri
import androidx.xr.scenecore.AnchorEntity
import androidx.xr.scenecore.Dimensions
import androidx.xr.scenecore.Session
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class SceneNode(
    val id: String,
    val modelUri: String? = null,
    val position: Dimensions,
    val nodeType: NodeType,
    val parentAnchorId: String? = null,
)

enum class NodeType {
    CREATURE,
    MOSS_PATCH,
    ROOT_TENDREL,
    PARTICLE_EMITTER,
    GLIMMER_OVERLAY,
}

data class ProceduralBiomeArt(
    val mossPatches: List<String> = emptyList(),
    val rootTendrils: List<String> = emptyList(),
    val particleEmitters: List<String> = emptyList(),
)

data class CreatureRenderInfo(
    val nodeId: String,
    val speciesName: String,
    val modelUri: Uri?,
    val anchorId: String,
)

class SceneCoreRenderLayer(private val session: Session) {

    private val _sceneNodes = MutableStateFlow<List<SceneNode>>(emptyList())
    val sceneNodes: StateFlow<List<SceneNode>> = _sceneNodes.asStateFlow()

    private val _proceduralArt = MutableStateFlow(ProceduralBiomeArt())
    val proceduralArt: StateFlow<ProceduralBiomeArt> = _proceduralArt.asStateFlow()

    private val _creatures = MutableStateFlow<List<CreatureRenderInfo>>(emptyList())
    val creatures: StateFlow<List<CreatureRenderInfo>> = _creatures.asStateFlow()

    private val activeAnchors = mutableMapOf<String, AnchorEntity>()

    fun registerCreature(
        nodeId: String,
        speciesName: String,
        modelUri: Uri?,
        position: Dimensions,
    ): AnchorEntity {
        val anchor = AnchorEntity.create(
            session,
            position,
            androidx.xr.scenecore.PlaneType.HORIZONTAL,
            androidx.xr.scenecore.PlaneSemantic.ANY,
        )
        activeAnchors[nodeId] = anchor

        val node = SceneNode(
            id = nodeId,
            modelUri = modelUri?.toString(),
            position = position,
            nodeType = NodeType.CREATURE,
            parentAnchorId = anchor.toString(),
        )
        _sceneNodes.value = _sceneNodes.value + node
        _creatures.value = _creatures.value + CreatureRenderInfo(
            nodeId = nodeId,
            speciesName = speciesName,
            modelUri = modelUri,
            anchorId = anchor.toString(),
        )
        return anchor
    }

    fun spawnMossPatch(nodeId: String, position: Dimensions) {
        val node = SceneNode(
            id = nodeId,
            position = position,
            nodeType = NodeType.MOSS_PATCH,
        )
        _sceneNodes.value = _sceneNodes.value + node
        val current = _proceduralArt.value
        _proceduralArt.value = current.copy(mossPatches = current.mossPatches + nodeId)
    }

    fun spawnRootTendril(nodeId: String, position: Dimensions) {
        val node = SceneNode(
            id = nodeId,
            position = position,
            nodeType = NodeType.ROOT_TENDREL,
        )
        _sceneNodes.value = _sceneNodes.value + node
        val current = _proceduralArt.value
        _proceduralArt.value = current.copy(rootTendrils = current.rootTendrils + nodeId)
    }

    fun spawnParticleEmitter(nodeId: String, position: Dimensions) {
        val node = SceneNode(
            id = nodeId,
            position = position,
            nodeType = NodeType.PARTICLE_EMITTER,
        )
        _sceneNodes.value = _sceneNodes.value + node
        val current = _proceduralArt.value
        _proceduralArt.value = current.copy(particleEmitters = current.particleEmitters + nodeId)
    }

    fun removeNode(nodeId: String) {
        activeAnchors.remove(nodeId)
        _sceneNodes.value = _sceneNodes.value.filter { it.id != nodeId }
        _creatures.value = _creatures.value.filter { it.nodeId != nodeId }
        val art = _proceduralArt.value
        _proceduralArt.value = art.copy(
            mossPatches = art.mossPatches.filter { it != nodeId },
            rootTendrils = art.rootTendrils.filter { it != nodeId },
            particleEmitters = art.particleEmitters.filter { it != nodeId },
        )
    }

    fun getCreatureAnchor(nodeId: String): AnchorEntity? {
        return activeAnchors[nodeId]
    }

    fun clear() {
        activeAnchors.clear()
        _sceneNodes.value = emptyList()
        _creatures.value = emptyList()
        _proceduralArt.value = ProceduralBiomeArt()
    }
}
