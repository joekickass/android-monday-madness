package com.joekickass.mondaymadness.view

import com.joekickass.mondaymadness.model.Event
import com.joekickass.mondaymadness.model.Timer
import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [IntervalView]
 *
 * TODO: Refactor me!
 */
class IntervalTimer(private val view: IntervalView,
                    workInMillis: Long,
                    restInMillis: Long,
                    repetitions: Int) {

    class WorkFinishedEvent {
        companion object : Event<WorkFinishedEvent>()
        fun emit() = Companion.emit(this)
    }

    class RestFinishedEvent {
        companion object : Event<RestFinishedEvent>()
        fun emit() = Companion.emit(this)
    }

    class WorkoutFinishedEvent {
        companion object : Event<WorkoutFinishedEvent>()
        fun emit() = Companion.emit(this)
    }

    private val workout = Workout(workInMillis, restInMillis, repetitions)

    private var timer = Timer(workout.time)

    init {
        view.init(timer)
        Timer.IntervalFinishedEvent on { intervalFinished() }
    }

    fun start() {
        view.start()
    }

    fun pause() {
        view.pause()
    }

    val isRunning: Boolean
        get() = timer.isRunning

    private fun intervalFinished() {

        // Notify listeners
        if (workout.work) WorkFinishedEvent().emit()
        if (workout.rest) RestFinishedEvent().emit()

        // Start new if there are any intervals left
        if (workout.hasNextInterval()) {
            workout.nextInterval()
            timer = Timer(workout.time)
            view.init(timer)
            view.start()
        } else {
            WorkoutFinishedEvent().emit()
        }
    }
}