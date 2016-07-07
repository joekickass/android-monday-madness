package com.joekickass.mondaymadness.intervaltimer

import android.os.SystemClock
import com.joekickass.mondaymadness.model.Timer
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.CopyOnWriteArrayList

/**
 * Controller for [IntervalTimerView]

 * Handles two types of intervals; WORK and REST. It is also possible to create a chain of intervals
 * by specifying the number of repetitions.
 */
class IntervalTimer(private val view: IntervalTimerView,
                    private val workInMillis: Long,
                    private val restInMillis: Long,
                    private val repetitions: Int) {

    interface IntervalTimerListener {
        fun onWorkFinished()
        fun onRestFinished()
        fun onIntervalTimerFinished()
    }

    private var workQueue: Queue<Interval>

    private var current: Interval

    private var timer: Timer = Timer(0)

    // TODO: Create special notifier/listener class with thread handling?
    private val mListeners = CopyOnWriteArrayList<IntervalTimerListener>()

    init {
        workQueue = generateWorkQueue(workInMillis, restInMillis, repetitions)
        current = workQueue.poll()
        timer = Timer(current.time)
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
            when (current.type) {
                IntervalTimer.Interval.IntervalType.WORK -> listener.onWorkFinished()
                IntervalTimer.Interval.IntervalType.REST -> listener.onRestFinished()
            }
        }

        // Start new if there are any intervals left
        if (!workQueue.isEmpty()) {
            current = workQueue.poll()
            timer = Timer(current.time)
            view.init(timer, { intervalFinished() })
            view.start()
        } else {
            for (listener in mListeners) {
                listener.onIntervalTimerFinished()
            }
        }
    }

    private fun generateWorkQueue(work: Long, rest: Long, repetitions: Int): Queue<Interval> {
        val ret = LinkedList<Interval>()
        for (i in 1..repetitions) {
            ret.add(Interval(Interval.IntervalType.WORK, work))
            ret.add(Interval(Interval.IntervalType.REST, rest))
        }
        return ret
    }

    private class Interval internal constructor(internal var type: Interval.IntervalType, internal var time: Long) {
        enum class IntervalType {
            WORK, REST
        }
    }
}