package com.joekickass.mondaymadness.model

class Workout(private val queue: IntervalQueue, internal val timer: Timer = Timer(queue.time)) {

    var onWorkRunning: () -> Unit = {}
    var onWorkPaused: () -> Unit = {}
    var onWorkFinished: () -> Unit = {}
    var onRestRunning: () -> Unit = {}
    var onRestPaused: () -> Unit = {}
    var onRestFinished: () -> Unit = {}
    var onWorkoutFinished: () -> Unit = {}

    init {
        timer.onRunning = { intervalRunning() }
        timer.onPaused = { intervalPaused() }
        timer.onFinished = { intervalFinished() }
    }

    fun start(): Workout {
        timer.start()
        return this
    }

    fun pause(): Workout {
        timer.pause()
        return this
    }

    private fun intervalRunning() {
        when {
            queue.work -> onWorkRunning()
            queue.rest -> onRestRunning()
        }
    }

    private fun intervalPaused() {
        when {
            queue.work -> onWorkPaused()
            queue.rest -> onRestPaused()
        }
    }

    private fun intervalFinished() {
        when {
            queue.work -> onWorkFinished()
            queue.rest -> onRestFinished()
        }

        // Start new if there are any intervals left
        if (queue.hasNextInterval()) {
            queue.nextInterval()
            timer.reset(queue.time)
            start()
        } else {
            onWorkoutFinished()
        }
    }

    val running: Boolean
        get() = timer.running
}