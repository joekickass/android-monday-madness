package com.joekickass.mondaymadness.model

import android.os.SystemClock
import com.joekickass.mondaymadness.model.Timer.State.*
import java.lang.Double.toString
import kotlin.properties.Delegates.observable

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

    class IntervalFinishedEvent {
        companion object : Event<IntervalFinishedEvent>()
        fun emit() = Companion.emit(this)
    }

    private var startTimeInMillis: Long = 0
    private var timeLeftInMillis: Long = 0
    private var state: State by observable(INITIALIZED) { prop, old, new ->
        if (new == FINISHED) IntervalFinishedEvent().emit()
    }

    init {
        if (timeInMillis < 0L) throw IllegalArgumentException("Argument must not be less than zero")
        if (timeInMillis == 0L) finish()
        timeLeftInMillis = timeInMillis
    }

    fun start(): Timer {
        if (isRunning) return this
        if (isFinished) throw IllegalStateException("Timer already finished")

        startTimeInMillis = clock.elapsedRealtime()
        if (isPaused) startTimeInMillis -= timeInMillis - timeLeftInMillis

        state = RUNNING

        return this
    }

    fun pause(): Timer {
        if (isInitialized) return this
        if (isPaused) return this
        if (isFinished) throw IllegalStateException("Timer already finished")

        startTimeInMillis = 0

        state = PAUSED

        return this
    }

    private fun finish(): Timer {
        startTimeInMillis = 0
        timeLeftInMillis = 0

        state = FINISHED

        return this
    }

    fun tick(): Timer {
        // Calculate new progress only if we're running, else keep the old value...
        if (isRunning) {
            timeLeftInMillis = timeInMillis - (clock.elapsedRealtime() - startTimeInMillis)
        }

        // Finish if we reached 0
        if (timeLeftInMillis <= 0) {
            finish()
        }

        return this
    }

    val isRunning: Boolean
        get() = state == RUNNING

    val isPaused: Boolean
        get() = state == PAUSED

    val isInitialized: Boolean
        get() = state == INITIALIZED

    val isFinished: Boolean
        get() = state == FINISHED

    val text: String
        get() = toString( (timeLeftInMillis / 100).toDouble() / 10 )

    val fraction: Double
        get() = timeLeftInMillis.toDouble() / timeInMillis

    /**
     * Possible timer states
     */
    internal enum class State {
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