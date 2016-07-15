package com.joekickass.mondaymadness.view

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

    interface IntervalTimerListener {
        fun onWorkFinished()
        fun onRestFinished()
        fun onIntervalTimerFinished()
    }

    private val workout = Workout(workInMillis, restInMillis, repetitions)

    private var timer = Timer(workout.time, { intervalFinished() })

    // TODO: Create special notifier/listener class with thread handling?
    private val mListeners = mutableListOf<IntervalTimerListener>()

    init {
        view.init(timer)
    }

    fun start() {
        view.start()
    }

    fun pause() {
        view.pause()
    }

    val isRunning: Boolean
        get() = timer.isRunning

    fun addListener(listener: IntervalTimerListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: IntervalTimerListener) {
        mListeners.remove(listener)
    }

    private fun intervalFinished() {

        // Notify listeners
        for (listener in mListeners) {
            if (workout.work) listener.onWorkFinished()
            if (workout.rest) listener.onRestFinished()
        }

        // Start new if there are any intervals left
        if (workout.hasNext()) {
            workout.next()
            timer = Timer(workout.time, { intervalFinished() })
            view.init(timer)
            view.start()
        } else {
            for (listener in mListeners) {
                listener.onIntervalTimerFinished()
            }
        }
    }
}