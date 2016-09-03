package com.joekickass.mondaymadness.model

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException

class WorkoutTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()!!

    @Test
    fun createNewWorkoutSession() {
        Assert.assertNotNull(Workout(IntervalQueue(10, 10, 1)))
    }

    @Test
    fun startingWorkoutSessionFiresEvent() {
        var called = false
        Workout.WorkRunningEvent on { called = true}
        Assert.assertFalse(called)

        val session = Workout(IntervalQueue(10, 10, 1))
        session.start()
        Assert.assertTrue(called)
    }
}
