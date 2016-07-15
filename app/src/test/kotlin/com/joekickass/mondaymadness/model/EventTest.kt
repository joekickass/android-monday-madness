package com.joekickass.mondaymadness.model

import org.junit.Assert
import org.junit.Test

class EventTest {

    class TestEvent {
        companion object : Event<TestEvent>()
        fun signal() = Companion.signal(this)
    }

    @Test
    fun createNewEvent() {
        Assert.assertNotNull(TestEvent())
    }

    @Test
    fun signalEvent() {
        var called = false
        TestEvent on { called = true }
        Assert.assertFalse(called)

        TestEvent().signal()
        Assert.assertTrue(called)
    }
}