package com.joekickass.mondaymadness.view

import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [IntervalView]
 */
class IntervalViewController(private val view: IntervalView, private val workout: Workout) {

    var onRunning: () -> Unit = {}
    var onPaused: () -> Unit = {}
    var onFinished: () -> Unit = {}

    init {
        view.init(workout.timer)
        workout.onWorkRunning = { onRunning() }
        workout.onWorkPaused = { onPaused() }
        workout.onWorkoutFinished = { onFinished() }
    }

    fun onClick() {
        when {
            workout.running -> {
                workout.pause()
                view.pause()
            }
            else -> {
                workout.start()
                view.start()
            }
        }
    }
}