package com.joekickass.mondaymadness.model

class Workout (val queue: IntervalQueue) {

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

    private var timer = Timer(queue.time)

    init {
        Timer.TimerRunningEvent on { intervalRunning() }
        Timer.TimerPausedEvent on { intervalPaused() }
        Timer.TimerFinishedEvent on { intervalFinished() }
    }

    fun start() {
        timer.start()
    }

    fun pause() {
        timer.pause()
    }

    fun stop() {
        timer = Timer(queue.time)
    }

    private fun intervalRunning() {
        if (queue.work) WorkRunningEvent().signal()
        if (queue.rest) RestRunningEvent().signal()
    }

    private fun intervalPaused() {
        if (queue.work) WorkPausedEvent().signal()
        if (queue.rest) RestPausedEvent().signal()
    }

    private fun intervalFinished() {
        if (queue.work) WorkFinishedEvent().signal()
        if (queue.rest) RestFinishedEvent().signal()

        // Start new if there are any intervals left
        if (queue.hasNextInterval()) {
            queue.nextInterval()
            timer = Timer(queue.time)
            start()
        } else {
            WorkoutFinishedEvent().signal()
        }
    }
}