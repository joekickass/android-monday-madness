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
    fun startingWorkFiresEvent() {
        var called = false
        val workout = Workout(IntervalQueue(10, 0, 1), Timer(10, ClockMock()))
        workout.onWorkRunning = { called = true}
        Assert.assertFalse(called)

        workout.start()
        Assert.assertTrue(called)
    }

    @Test
    fun pausingWorkFiresEvent() {
        var called = false
        val workout = Workout(IntervalQueue(10, 0, 1), Timer(10, ClockMock()))
        workout.onWorkPaused = { called = true}
        Assert.assertFalse(called)

        workout.start().pause()
        Assert.assertTrue(called)
    }

    @Test
    fun finishingWorkFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10)))
        val workout = Workout(IntervalQueue(10, 0, 1), timer)
        workout.onWorkFinished = { called = true}
        Assert.assertFalse(called)

        workout.start()
        timer.tick()

        Assert.assertTrue(called)
    }

    @Test
    fun startingRestFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30)))
        val workout = Workout(IntervalQueue(10, 10, 1), timer)
        workout.onRestRunning = { called = true}
        Assert.assertFalse(called)

        workout.start()
        timer.tick()

        Assert.assertTrue(called)
    }

    @Test
    fun pausingRestFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30)))
        val workout = Workout(IntervalQueue(10, 10, 1), timer)
        workout.onRestPaused = { called = true}
        Assert.assertFalse(called)

        workout.start()
        timer.tick()
        workout.pause()

        Assert.assertTrue(called)
    }

    @Test
    fun finishingRestFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30)))
        val workout = Workout(IntervalQueue(10, 10, 1), timer)
        workout.onRestFinished = { called = true}
        Assert.assertFalse(called)

        workout.start()
        timer.tick()
        timer.tick()

        Assert.assertTrue(called)
    }

    private class ClockMock(ticks: List<Long> = listOf(0)) : Timer.ISystemClock {
        val iter = ticks.listIterator()
        override fun elapsedRealtime(): Long {
            return iter.next()
        }
    }
}
