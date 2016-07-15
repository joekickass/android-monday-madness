package com.joekickass.mondaymadness.model

/**
 * Simple Event class inspired by https://nvbn.github.io/2016/04/28/kotlin-events
 *
 * TODO: handler references can cause memory leaks. Need fix.
 */
open class Event<T> {
    var handlers = listOf<(T) -> Unit>()

    infix fun on(handler: (T) -> Unit) {
        handlers += handler
    }

    fun emit(event: T) {
        for (subscriber in handlers) {
            subscriber(event)
        }
    }
}
