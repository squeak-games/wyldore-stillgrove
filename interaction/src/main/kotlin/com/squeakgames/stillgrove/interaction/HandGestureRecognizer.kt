package com.squeakgames.stillgrove.interaction

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sqrt

class HandGestureRecognizer {

    private val _currentGesture = MutableStateFlow<GestureType?>(null)
    val currentGesture: StateFlow<GestureType?> = _currentGesture.asStateFlow()

    fun processJoints(joints: List<JointPosition>): GestureType? {
        val gesture = recognize(joints)
        _currentGesture.value = gesture
        return gesture
    }

    fun reset() {
        _currentGesture.value = null
    }

    private fun recognize(joints: List<JointPosition>): GestureType? {
        if (joints.size < 21) return null
        val wrist = joints[0]
        val thumbTip = joints[4]
        val indexTip = joints[8]
        val indexMcp = joints[5]
        val middleTip = joints[12]
        val middlePip = joints[10]
        val ringTip = joints[16]
        val pinkyTip = joints[20]

        val thumbIndexDist = distance(thumbTip, indexTip)
        val indexMiddleDist = distance(indexTip, middleTip)

        val palmNormal = computePalmNormal(joints)
        val wristToMiddle = normalize(subtract(middleTip, wrist))

        val dot = dotProduct(palmNormal, wristToMiddle)

        if (thumbIndexDist < 0.03f && indexMiddleDist < 0.03f) {
            return GestureType.PINCH
        }

        if (dot < -0.5f && isFingersCurled(joints)) {
            return GestureType.SHELTERING
        }

        if (dot > 0.5f && areFingersSpread(joints)) {
            return GestureType.OFFERING
        }

        val handVelocity = computeHandVelocity(joints)
        if (handVelocity < 0.1f && areFingersRelaxed(joints)) {
            return GestureType.GUIDING
        }

        return GestureType.CALLING
    }

    private fun isFingersCurled(joints: List<JointPosition>): Boolean {
        val tipToPip = listOf(
            distance(joints[8], joints[6]),
            distance(joints[12], joints[10]),
            distance(joints[16], joints[14]),
            distance(joints[20], joints[18]),
        )
        return tipToPip.all { it < 0.05f }
    }

    private fun areFingersSpread(joints: List<JointPosition>): Boolean {
        val spread = listOf(
            distance(joints[5], joints[9]),
            distance(joints[9], joints[13]),
            distance(joints[13], joints[17]),
        )
        return spread.all { it > 0.04f }
    }

    private fun areFingersRelaxed(joints: List<JointPosition>): Boolean {
        val tipToPip = listOf(
            distance(joints[8], joints[6]),
            distance(joints[12], joints[10]),
            distance(joints[16], joints[14]),
            distance(joints[20], joints[18]),
        )
        val avg = tipToPip.average().toFloat()
        return avg in 0.03f..0.12f
    }

    private fun computePalmNormal(joints: List<JointPosition>): JointPosition {
        val wrist = joints[0]
        val indexMcp = joints[5]
        val pinkyMcp = joints[17]
        val v1 = subtract(indexMcp, wrist)
        val v2 = subtract(pinkyMcp, wrist)
        return crossProduct(v1, v2)
    }

    private fun computeHandVelocity(joints: List<JointPosition>): Float {
        return 0f
    }

    enum class GestureType {
        OFFERING,
        SHELTERING,
        PINCH,
        GUIDING,
        CALLING,
    }
}

data class JointPosition(val x: Float, val y: Float, val z: Float)

fun distance(a: JointPosition, b: JointPosition): Float {
    val dx = a.x - b.x; val dy = a.y - b.y; val dz = a.z - b.z
    return sqrt(dx * dx + dy * dy + dz * dz)
}

fun subtract(a: JointPosition, b: JointPosition) =
    JointPosition(a.x - b.x, a.y - b.y, a.z - b.z)

fun normalize(v: JointPosition): JointPosition {
    val len = sqrt(v.x * v.x + v.y * v.y + v.z * v.z)
    return if (len > 0f) JointPosition(v.x / len, v.y / len, v.z / len) else v
}

fun dotProduct(a: JointPosition, b: JointPosition) =
    a.x * b.x + a.y * b.y + a.z * b.z

fun crossProduct(a: JointPosition, b: JointPosition) =
    JointPosition(
        a.y * b.z - a.z * b.y,
        a.z * b.x - a.x * b.z,
        a.x * b.y - a.y * b.x,
    )
