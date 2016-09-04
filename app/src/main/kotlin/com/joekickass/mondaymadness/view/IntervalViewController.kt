package com.joekickass.mondaymadness.view

import com.joekickass.mondaymadness.model.IntervalQueue
import com.joekickass.mondaymadness.model.Timer
import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [IntervalView]
 */
class IntervalViewController(private val view: IntervalView, queue: IntervalQueue) {

    private val workout : Workout

    var onRunning: () -> Unit = {}
    var onPaused: () -> Unit = {}
    var onFinished: () -> Unit = {}

    init {
        val timer = Timer(queue.time)
        view.init(timer)
        workout = Workout(queue, timer)
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