package com.joekickass.mondaymadness.view

import com.joekickass.mondaymadness.model.Event
import com.joekickass.mondaymadness.model.IntervalQueue
import com.joekickass.mondaymadness.model.Timer
import com.joekickass.mondaymadness.model.Workout

/**
 * Controller for [IntervalView]
 *
 * TODO: Refactor me!
 */
class IntervalViewController(private val view: IntervalView,
                             workInMillis: Long,
                             restInMillis: Long,
                             repetitions: Int) {

    class WorkRunningEvent {
        companion object : Event<WorkRunningEvent>()
        fun signal() = Companion.signal(this)
    }

    class WorkPausedEvent {
        companion object : Event<WorkPausedEvent>()
        fun signal() = Companion.signal(this)
    }

    class WorkFinishedEvent {
        companion object : Event<WorkFinishedEvent>()
        fun signal() = Companion.signal(this)
    }

    class RestRunningEvent {
        companion object : Event<RestRunningEvent>()
        fun signal() = Companion.signal(this)
    }

    class RestPausedEvent {
        companion object : Event<RestPausedEvent>()
        fun signal() = Companion.signal(this)
    }

    class RestFinishedEvent {
        companion object : Event<RestFinishedEvent>()
        fun signal() = Companion.signal(this)
    }

    class WorkoutFinishedEvent {
        companion object : Event<WorkoutFinishedEvent>()
        fun signal() = Companion.signal(this)
    }

    private val queue = IntervalQueue(workInMillis, restInMillis, repetitions)

    private var timer = Timer(queue.time)

    init {
        view.init(timer)
        Timer.TimerRunningEvent on { intervalRunning() }
        Timer.TimerPausedEvent on { intervalPaused() }
        Timer.TimerFinishedEvent on { intervalFinished() }
    }

    fun start() {
        view.start()
    }

    fun pause() {
        view.pause()
    }

    val isRunning: Boolean
        get() = timer.isRunning


    private fun intervalRunning() {
        if (queue.work) WorkRunningEvent().signal()
        if (queue.rest) RestRunningEvent().signal()
    }

    private fun intervalPaused() {
        if (queue.work) WorkPausedEvent().signal()
        if (queue.rest) RestPausedEvent().signal()
    }

    private fun intervalFinished() {

        // Notify listeners
        if (queue.work) WorkFinishedEvent().signal()
        if (queue.rest) RestFinishedEvent().signal()

        // Start new if there are any intervals left
        if (queue.hasNextInterval()) {
            queue.nextInterval()
            timer = Timer(queue.time)
            view.init(timer)
            view.start()
        } else {
            WorkoutFinishedEvent().signal()
        }
    }
}