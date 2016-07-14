package com.joekickass.mondaymadness.intervaltimer

import com.joekickass.mondaymadness.model.Timer
import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [IntervalTimerView]
 */
class IntervalTimer(private val view: IntervalTimerView,
                    workInMillis: Long,
                    restInMillis: Long,
                    repetitions: Int) {

    interface IntervalTimerListener {
        fun onWorkFinished()
        fun onRestFinished()
        fun onIntervalTimerFinished()
    }

    private val workout = Workout(workInMillis, restInMillis, repetitions)

    private var timer: Timer = Timer(0)

    // TODO: Create special notifier/listener class with thread handling?
    private val mListeners = mutableListOf<IntervalTimerListener>()

    init {
        timer = Timer(workout.time)
        view.init(timer, { intervalFinished() })
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
            timer = Timer(workout.time)
            view.init(timer, { intervalFinished() })
            view.start()
        } else {
            for (listener in mListeners) {
                listener.onIntervalTimerFinished()
            }
        }
    }
}