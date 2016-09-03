package com.joekickass.mondaymadness.model

/**
 * A {@link IntervalQueue} is a series of {@link Interval}s, either work or rest.
 */
class IntervalQueue(val workInMillis: Long, val restInMillis: Long, repetitions: Int = 1) {

    private enum class IntervalType { WORK, REST }

    private data class Interval(val type: IntervalType, val time: Long)

    private val intervals: List<Interval> =
        (1..repetitions)
        .flatMap {
            if (restInMillis == 0L) listOf(Interval(IntervalType.WORK, workInMillis))
            else listOf(Interval(IntervalType.WORK, workInMillis), Interval(IntervalType.REST, restInMillis))
        }.toList()

    private val iter: ListIterator<Interval> = intervals.listIterator()

    private var current : Interval

    init {
        if (workInMillis <= 0) throw IllegalArgumentException("Work time must be a positive value")
        if (restInMillis < 0) throw IllegalArgumentException("Rest time cannot be a negative value")
        if (restInMillis == 0L && repetitions != 1) throw IllegalArgumentException("Skipping rest only allowed for single interval")
        if (repetitions < 1) throw IllegalArgumentException("Repetitions must be a positive value")
        current = iter.next()
    }

    val count : Int
        get() = intervals.count()

    val work : Boolean
        get() = current.type == IntervalType.WORK

    val rest : Boolean
        get() = current.type == IntervalType.REST

    val time : Long
        get() = current.time

    fun hasNextInterval() : Boolean {
        return iter.hasNext()
    }

    fun nextInterval() : IntervalQueue {
        current = iter.next()
        return this
    }
}