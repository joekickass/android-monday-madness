package com.joekickass.mondaymadness.model

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*

class IntervalQueueTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()!!

    @Test
    fun createNewWorkout() {
        Assert.assertNotNull(IntervalQueue(10, 10, 1))
    }

    @Test
    fun newWorkoutWithNegativeWorktimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(-10, 10, 1)
    }

    @Test
    fun newWorkoutWithZeroWorktimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(0, 10, 1)
    }

    @Test
    fun newWorkoutWithNegativeResttimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(10, -10, 1)
    }

    @Test
    fun newWorkoutWithZeroResttimeOnlyAllowedIfSingleRepetition() {
        Assert.assertNotNull(IntervalQueue(10, 0, 1))
    }

    @Test
    fun newWorkoutWithZeroResttimeOnlyAllowedIfSingleRepetition2() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(10, 0, 2)
    }

    @Test
    fun newWorkoutWithNegativeRepetitionsNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(10, 10, -10)
    }

    @Test
    fun newWorkoutWithZeroRepetitionsNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        IntervalQueue(10, 10, 0)
    }

    @Test
    fun WorkoutWith10RepetitionsShouldHave20Intervals() {
        Assert.assertEquals(20, IntervalQueue(10, 10, 10).count)
    }

    @Test
    fun WorkoutWith1RepetitionShouldHave1WorkInterval() {
        Assert.assertTrue(IntervalQueue(10, 10, 1).work)
    }

    @Test
    fun WorkoutWith1RepetitionShouldHave1RestIntervalAfterWorkInterval() {
        val workout = IntervalQueue(10, 10, 1)
        workout.nextInterval()
        Assert.assertTrue(workout.rest)
    }

    @Test
    fun WorkoutWith1RepetitionButNoRestTimeShouldHave1WorkIntervalOnly() {
        Assert.assertEquals(1, IntervalQueue(10, 0, 1).count)
    }

    @Test
    fun ItIsPossibleToCheckIfThereAreMoreIntervals() {
        val workout = IntervalQueue(10, 10, 3)
        Assert.assertTrue(workout.nextInterval().nextInterval().hasNextInterval())
    }

    @Test
    fun NoMoreIntervalsAfterLastDuh() {
        val workout = IntervalQueue(10, 10, 3)
        Assert.assertFalse(workout.nextInterval().nextInterval().nextInterval().nextInterval().nextInterval().hasNextInterval())
    }

    @Test
    fun TryingToGetMoreIntervalsThanExistIsNotAllowed() {
        exception.expect(NoSuchElementException::class.java)
        val workout = IntervalQueue(10, 10, 3)
        workout.nextInterval().nextInterval().nextInterval().nextInterval().nextInterval().nextInterval()
    }

    @Test
    fun AllWorkIntervalsShouldHaveCorrectTime() {
        val workout = IntervalQueue(15, 20, 3)
        val work1 = workout.time
        val work2 = workout.nextInterval().nextInterval().time
        val work3 = workout.nextInterval().nextInterval().time
        val arr = listOf(work1, work2, work3)
        Assert.assertTrue(arr.all { it == 15L })
    }

    @Test
    fun AllRestIntervalsShouldHaveCorrectTime() {
        val workout = IntervalQueue(10, 20, 3)
        val rest1 = workout.nextInterval().time
        val rest2 = workout.nextInterval().nextInterval().time
        val rest3 = workout.nextInterval().nextInterval().time
        val arr = listOf(rest1, rest2, rest3)
        Assert.assertTrue(arr.all { it == 20L })
    }
}
