package com.squeakgames.stillgrove.interaction

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class HandGestureRecognizerTest {

    @Test
    fun noJoints_returnsNull() {
        val recognizer = HandGestureRecognizer()
        assertNull(recognizer.processJoints(emptyList()))
    }

    @Test
    fun defaultsToCalling() {
        val recognizer = HandGestureRecognizer()
        val joints = List(21) { JointPosition(it.toFloat(), 0f, 0f) }
        assertEquals(HandGestureRecognizer.GestureType.CALLING, recognizer.processJoints(joints))
    }

    @Test
    fun reset_clearsGesture() {
        val recognizer = HandGestureRecognizer()
        val joints = List(21) { JointPosition(it.toFloat(), 0f, 0f) }
        recognizer.processJoints(joints)
        recognizer.reset()
        assertNull(recognizer.currentGesture.value)
    }

    @Test
    fun distance_calculatesCorrectly() {
        val a = JointPosition(0f, 0f, 0f)
        val b = JointPosition(3f, 4f, 0f)
        assertEquals(5f, distance(a, b), 0.001f)
    }

    @Test
    fun subtract_calculatesCorrectly() {
        val result = subtract(JointPosition(5f, 3f, 1f), JointPosition(2f, 1f, 1f))
        assertEquals(JointPosition(3f, 2f, 0f), result)
    }

    @Test
    fun normalize_returnsUnitVector() {
        val v = normalize(JointPosition(3f, 0f, 0f))
        assertEquals(JointPosition(1f, 0f, 0f), v)
        assertEquals(1f, distance(v, JointPosition(0f, 0f, 0f)), 0.001f)
    }

    @Test
    fun dotProduct_calculatesCorrectly() {
        val result = dotProduct(JointPosition(1f, 0f, 0f), JointPosition(0f, 1f, 0f))
        assertEquals(0f, result, 0.001f)
    }

    @Test
    fun crossProduct_calculatesCorrectly() {
        val result = crossProduct(JointPosition(1f, 0f, 0f), JointPosition(0f, 1f, 0f))
        assertEquals(JointPosition(0f, 0f, 1f), result)
    }
}
