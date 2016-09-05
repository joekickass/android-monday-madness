package com.joekickass.mondaymadness.view

import com.joekickass.mondaymadness.model.IntervalQueue
import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [WorkoutView]
 */
class WorkoutViewController(private val view: WorkoutView, queue: IntervalQueue) {

    private val workout : Workout

    var onRunning: () -> Unit = {}
    var onPaused: () -> Unit = {}
    var onRest: () -> Unit = {}
    var onFinished: () -> Unit = {}

    init {
        workout = Workout(queue)
        workout.onWorkRunning = { onRunning() }
        workout.onWorkPaused = { onPaused() }
        workout.onRestRunning = { onRest() }
        workout.onWorkoutFinished = { onFinished() }
        view.init(workout)
    }

    fun onClick() {
        view.click()
    }
}