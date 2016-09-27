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

    @Test
    fun startingNewWorkFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30, 40)))
        val workout = Workout(IntervalQueue(10, 10, 2), timer)
        workout.onWorkRunning = { called = true}
        Assert.assertFalse(called)

        workout.start() // 1st work running
        timer.tick()    // 1st work finishes, 1st rest running
        called = false  // reset flag
        timer.tick()    // 1st rest finishes, 2nd work running

        Assert.assertTrue(called)
    }

    @Test
    fun startingNewRestFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30, 40, 50, 60)))
        val workout = Workout(IntervalQueue(10, 10, 2), timer)
        workout.onRestRunning = { called = true}
        Assert.assertFalse(called)

        workout.start() // 1st work running
        timer.tick()    // 1st work finishes, 1st rest running
        called = false  // reset flag
        timer.tick()    // 1st rest finishes, 2nd work running
        timer.tick()    // 2nd work finishes, 2nd rest running

        Assert.assertTrue(called)
    }

    @Test
    fun finishingWorkoutFiresEvent() {
        var called = false
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30, 40, 50, 60, 70)))
        val workout = Workout(IntervalQueue(10, 10, 2), timer)
        workout.onWorkoutFinished = { called = true}
        Assert.assertFalse(called)

        workout.start() // 1st work running
        timer.tick()    // 1st work finishes, 1st rest running
        timer.tick()    // 1st rest finishes, 2nd work running
        timer.tick()    // 2nd work finishes, 2nd rest running
        timer.tick()    // 2nd rest finishes, workout done

        Assert.assertTrue(called)
    }

    @Test
    fun startingWorkoutAfterFinishIsNotValid() {
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30)))
        val workout = Workout(IntervalQueue(10, 10, 1), timer)

        workout.start() // 1st work running
        timer.tick()    // 1st work finishes, 1st rest running
        timer.tick()    // 1st rest finishes, workout done

        exception.expect(IllegalStateException::class.java)
        workout.start()
    }

    @Test
    fun pausingWorkoutAfterFinishIsNotValid() {
        val timer = Timer(10, ClockMock(listOf(0, 10, 20, 30)))
        val workout = Workout(IntervalQueue(10, 10, 1), timer)

        workout.start() // 1st work running
        timer.tick()    // 1st work finishes, 1st rest running
        timer.tick()    // 1st rest finishes, workout done

        exception.expect(IllegalStateException::class.java)
        workout.pause()
    }

    private class ClockMock(ticks: List<Long> = listOf(0)) : Timer.ISystemClock {
        val iter = ticks.listIterator()
        override fun elapsedRealtime(): Long {
            return iter.next()
        }
    }
}
