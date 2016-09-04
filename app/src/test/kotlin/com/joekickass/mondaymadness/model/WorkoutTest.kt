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
        Assert.assertNotNull(Workout(IntervalQueue(10, 10, 1), Timer(10, ClockMock())))
    }

    @Test
    fun startingWorkoutSessionFiresEvent() {
        var called = false
        val workout = Workout(IntervalQueue(10, 10, 1), Timer(10, ClockMock()))
        workout.onWorkRunning = { called = true}
        Assert.assertFalse(called)

        workout.start()
        Assert.assertTrue(called)
    }

    private class ClockMock(ticks: List<Long> = listOf(0)) : Timer.ISystemClock {
        val iter = ticks.listIterator()
        override fun elapsedRealtime(): Long {
            return iter.next()
        }
    }
}
