package com.joekickass.mondaymadness.model

import android.os.SystemClock
import java.lang.Double.toString

/**
 * Countdown timer that counts down at irregular intervals (ticks).
 *
 * The timer is intended to be used together with Android views that animates themselves
 * continuously (as fast as allowed). The View.onDraw() method is a suitable place to invoke the
 * timer (Timer.tick()), since it is run on every animation cycle.
 *
 * As a consequence, the timer is not particularly exact. The impact on a single interval is
 * irrelevant, since the view cannot draw itself any faster than it already is. However, a long
 * series of intervals would probably be lagging a bit since the new timer won't start until the
 * view is finished with the last interval timer. I might need to rework this a bit later =)
 */
class Timer(val timeInMillis: Long, val clock: ISystemClock = Timer.SystemClockWrapper()) {

    private var state: State = State.INITIALIZED
    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0

    init {
        if (timeInMillis < 0) throw IllegalArgumentException("Argument must not be less than zero")
        timeLeftInMillis = timeInMillis
    }

    fun start() {
        if (isRunning) return
        if (isFinished) throw IllegalStateException("Timer already finished")

        startTimeInMillis = clock.elapsedRealtime()
        if (isPaused) startTimeInMillis -= timeInMillis - timeLeftInMillis

        state = State.RUNNING
    }

    fun pause() {
        if (isInitialized) return
        if (isPaused) return
        if (isFinished) throw IllegalStateException("Timer already finished")

        startTimeInMillis = 0

        state = State.PAUSED
    }

    fun finish() {
        startTimeInMillis = 0
        timeLeftInMillis = 0
        state = State.FINISHED
    }

    fun tick() {
        // Calculate new progress only if we're running, else keep the old value...
        if (isRunning) {
            timeLeftInMillis = timeInMillis - (clock.elapsedRealtime() - startTimeInMillis)
        }

        // Finish if we reached 0
        if (timeLeftInMillis <= 0) {
            finish()
        }
    }

    val isRunning: Boolean
        get() = state == State.RUNNING

    val isPaused: Boolean
        get() = state == State.PAUSED

    val isInitialized: Boolean
        get() = state == State.INITIALIZED

    val isFinished: Boolean
        get() = state == State.FINISHED

    val text: String
        get() = toString( (timeLeftInMillis / 100).toDouble() / 10 )

    val fraction: Double
        get() = timeLeftInMillis.toDouble() / timeInMillis

    /**
     * Possible timer states
     */
    private enum class State {
        FINISHED, INITIALIZED, RUNNING, PAUSED
    }

    /**
     * Decouples Timer from android.os.SystemClock
     */
    interface ISystemClock {
        fun elapsedRealtime(): Long
    }

    /**
     * Default implementation uses android.os.SystemClock
     */
    private class SystemClockWrapper : ISystemClock {
        override fun elapsedRealtime(): Long {
            return SystemClock.elapsedRealtime()
        }
    }
}