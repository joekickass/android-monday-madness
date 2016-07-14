package com.joekickass.mondaymadness.model

import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.rules.ExpectedException
import java.util.*

class WorkoutTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()!!

    @Test
    fun createNewWorkout() {
        Assert.assertNotNull(Workout(10, 10, 1))
    }

    @Test
    fun newWorkoutWithNegativeWorktimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(-10, 10, 1)
    }

    @Test
    fun newWorkoutWithZeroWorktimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(0, 10, 1)
    }

    @Test
    fun newWorkoutWithNegativeResttimeNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(10, -10, 1)
    }

    @Test
    fun newWorkoutWithZeroResttimeOnlyAllowedIfSingleRepetition() {
        Assert.assertNotNull(Workout(10, 0, 1))
    }

    @Test
    fun newWorkoutWithZeroResttimeOnlyAllowedIfSingleRepetition2() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(10, 0, 2)
    }

    @Test
    fun newWorkoutWithNegativeRepetitionsNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(10, 10, -10)
    }

    @Test
    fun newWorkoutWithZeroRepetitionsNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Workout(10, 10, 0)
    }

    @Test
    fun WorkoutWith10RepetitionsShouldHave20Intervals() {
        Assert.assertEquals(20, Workout(10, 10, 10).count)
    }

    @Test
    fun WorkoutWith1RepetitionShouldHave1WorkInterval() {
        Assert.assertTrue(Workout(10, 10, 1).work)
    }

    @Test
    fun WorkoutWith1RepetitionShouldHave1RestIntervalAfterWorkInterval() {
        val workout = Workout(10, 10, 1)
        workout.next()
        Assert.assertTrue(workout.rest)
    }

    @Test
    fun WorkoutWith1RepetitionButNoRestTimeShouldHave1WorkIntervalOnly() {
        Assert.assertEquals(1, Workout(10, 0, 1).count)
    }

    @Test
    fun ItIsPossibleToCheckIfThereAreMoreIntervals() {
        val workout = Workout(10, 10, 3)
        Assert.assertTrue(workout.next().next().hasNext())
    }

    @Test
    fun NoMoreIntervalsAfterLastDuh() {
        val workout = Workout(10, 10, 3)
        Assert.assertFalse(workout.next().next().next().next().next().hasNext())
    }

    @Test
    fun TryingToGetMoreIntervalsThanExistIsNotAllowed() {
        exception.expect(NoSuchElementException::class.java)
        val workout = Workout(10, 10, 3)
        workout.next().next().next().next().next().next()
    }

    @Test
    fun AllWorkIntervalsShouldHaveCorrectTime() {
        val workout = Workout(15, 20, 3)
        val work1 = workout.time
        val work2 = workout.next().next().time
        val work3 = workout.next().next().time
        val arr = listOf(work1, work2, work3)
        Assert.assertTrue(arr.all { it == 15L })
    }

    @Test
    fun AllRestIntervalsShouldHaveCorrectTime() {
        val workout = Workout(10, 20, 3)
        val rest1 = workout.next().time
        val rest2 = workout.next().next().time
        val rest3 = workout.next().next().time
        val arr = listOf(rest1, rest2, rest3)
        Assert.assertTrue(arr.all { it == 20L })
    }
}
