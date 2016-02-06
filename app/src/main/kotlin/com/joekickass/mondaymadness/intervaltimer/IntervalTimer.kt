package com.joekickass.mondaymadness.intervaltimer

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
                    private val repetitions: Int) : IntervalTimerView.Callback {

    interface IntervalTimerListener {
        fun onWorkFinished()
        fun onRestFinished()
        fun onIntervalTimerFinished()
    }

    private var workQueue: Queue<Interval>

    private var current: Interval

    // TODO: Create special notifier/listener class with thread handling?
    private val mListeners = CopyOnWriteArrayList<IntervalTimerListener>()

    init {
        view.registerCallback(this)
        workQueue = generateWorkQueue(workInMillis, restInMillis, repetitions)
        current = workQueue.poll()
        view.init(current.time)
    }

    fun start() {
        view.start()
    }

    fun pause() {
        view.pause()
    }

    val isRunning: Boolean
        get() = view.isRunning

    override fun onIntervalFinished() {

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
            view.init(current.time)
            view.start()
        } else {
            for (listener in mListeners) {
                listener.onIntervalTimerFinished()
            }
        }
    }

    fun addListener(listener: IntervalTimerListener) {
        mListeners.add(listener)
    }

    fun removeListener(listener: IntervalTimerListener) {
        mListeners.remove(listener)
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