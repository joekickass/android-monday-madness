package com.joekickass.mondaymadness.model

import com.joekickass.mondaymadness.model.Timer.ISystemClock
import org.junit.Assert
import org.junit.Test
import org.junit.Rule
import org.junit.rules.ExpectedException

class TimerTest {

    @Rule
    @JvmField
    val exception = ExpectedException.none()!!

    @Test
    fun createNewTimer() {
        Assert.assertNotNull(Timer(10))
    }

    @Test
    fun newTimerWithNegativeIntervalNotAllowed() {
        exception.expect(IllegalArgumentException::class.java)
        Timer(-20)
    }

    @Test
    fun newTimerWithZeroIntervalStartsInFinishedState() {
        val timer = Timer(0)
        Assert.assertTrue(timer.finished)
    }

    @Test
    fun newTimerStartsInInitState() {
        val timer = Timer(10)
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun startingATimerPutsItInRunningState() {
        val timer = Timer(10, ClockMock())
        timer.start()
        Assert.assertTrue(timer.running)
    }

    @Test
    fun startingATimerSignalsRunningEvent() {
        var called = false
        val t = Timer(10, ClockMock())
        t.onRunning = { called = true}
        Assert.assertFalse(called)

        t.start()
        Assert.assertTrue(called)
    }

    @Test
    fun startingTimerTwiceDoesNothing() {
        val timer = Timer(10, ClockMock())
        timer.start()
        timer.start()
        Assert.assertTrue(timer.running)
    }

    @Test
    fun startingTimerNotAllowedIfAlreadyFinished() {
        val timer = Timer(0, ClockMock())
        exception.expect(IllegalStateException::class.java)
        timer.start()
    }

    @Test
    fun pausingATimerBeforeStartingItDoesNothing() {
        val timer = Timer(10, ClockMock())
        timer.pause()
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun pausingARunningTimerPutsItInPausedState() {
        val timer = Timer(10, ClockMock())
        timer.start()
        timer.pause()
        Assert.assertTrue(timer.paused)
    }

    @Test
    fun pausingATimerSignalsPausedEvent() {
        var called = false
        val t = Timer(10, ClockMock())
        t.onRunning = { called = true}
        Assert.assertFalse(called)

        t.start().pause()
        Assert.assertTrue(called)
    }

    @Test
    fun resumingAPausedTimerPutsItInRunningState() {
        val timer = Timer(10, ClockMock(listOf(0, 5)))
        timer.start()
        timer.pause()
        timer.start()
        Assert.assertTrue(timer.running)
    }

    @Test
    fun resumingAPausedTimerSignalsRunningEvent() {
        var called = false
        val t = Timer(10, ClockMock(listOf(0, 5))).start().pause()
        t.onRunning = { called = true}
        Assert.assertFalse(called)

        t.start()
        Assert.assertTrue(called)
    }

    @Test
    fun pausingTimerNotAllowedIfAlreadyFinished() {
        val timer = Timer(0, ClockMock())
        exception.expect(IllegalStateException::class.java)
        timer.pause()
    }

    @Test
    fun timerIsInitializedAsFullTime() {
        val timer = Timer(10, ClockMock())
        Assert.assertEquals(1.0, timer.fraction, 0.0001)
    }

    @Test
    fun timerTickWhenNotRunningDoesNotCountDown1() {
        val timer = Timer(10, ClockMock(listOf(5)))
        val currTime = timer.fraction
        timer.tick()
        Assert.assertEquals(currTime, timer.fraction, 0.0001)
    }

    @Test
    fun timerTickWhenNotRunningDoesNotCountDown2() {
        val timer = Timer(10, ClockMock(listOf(5)))
        timer.start()
        timer.pause()
        val currTime = timer.fraction
        timer.tick()
        Assert.assertEquals(currTime, timer.fraction, 0.0001)
    }

    @Test
    fun timerTickWhenNotRunningDoesNotCountDown3() {
        val timer = Timer(10, ClockMock(listOf(0, 10)))
        val currTime = timer.fraction
        timer.tick()
        Assert.assertEquals(currTime, timer.fraction, 0.0001)
    }

    @Test
    fun timerTickCountsDownWithTimeFromISystemClock() {
        val timer = Timer(10, ClockMock(listOf(0, 5)))
        timer.start()
        timer.tick()
        Assert.assertEquals(0.5, timer.fraction, 0.0001)
    }

    @Test
    fun timerTickCountsDownUntilFinished1() {
        val timer = Timer(10, ClockMock(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
        timer.start()
             .tick().tick().tick().tick().tick()
             .tick().tick().tick().tick().tick()

        Assert.assertEquals(0.0, timer.fraction, 0.0001)
        Assert.assertTrue(timer.finished)
    }

    @Test
    fun timerTickCountsDownUntilFinished2() {
        var called = false
        val t = Timer(10, ClockMock(listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10)))
        t.onFinished = { called = true}
        Assert.assertFalse(called)

        t.start()
                .tick().tick().tick().tick().tick()
                .tick().tick().tick().tick().tick()

        Assert.assertTrue(called)
    }

    @Test
    fun resettingANewTimerUpdatesTime() {
        val timer = Timer(10, ClockMock())
        Assert.assertTrue(timer.initialized)

        timer.reset(10)
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun resettingANewTimerUpdatesTime2() {
        val timer = Timer(2000, ClockMock())
        Assert.assertEquals("2.0", timer.text)

        timer.reset(1000)
        Assert.assertEquals("1.0", timer.text)
    }

    @Test
    fun resettingAFinishedTimerResetsItToInitState() {
        val timer = Timer(0, ClockMock())
        Assert.assertTrue(timer.finished)

        timer.reset(0)
        Assert.assertTrue(timer.finished)
    }

    @Test
    fun resettingAFinishedTimerResetsItToInitState2() {
        val timer = Timer(10, ClockMock(listOf(0, 10)))
        timer.start().tick().tick()
        Assert.assertTrue(timer.finished)

        timer.reset(10)
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun resettingARunningTimerResetsItToInitState() {
        val timer = Timer(10, ClockMock())
        timer.start()
        Assert.assertTrue(timer.running)

        timer.reset(10)
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun resettingAPausedTimerResetsItToInitState() {
        val timer = Timer(10, ClockMock())
        timer.start().pause()
        Assert.assertTrue(timer.paused)

        timer.reset(10)
        Assert.assertTrue(timer.initialized)
    }

    @Test
    fun timerInitialText() {
        val timer = Timer(2000, ClockMock())
        Assert.assertEquals("2.0", timer.text)
    }

    @Test
    fun timerInitialTextTwoHundredthOfASecondDisplaysFractionOfASecond() {
        val timer = Timer(200, ClockMock())
        Assert.assertEquals("0.2", timer.text)
    }

    @Test
    fun timerInitialTextLessThanOneHundredthOfASecondDisplaysZero() {
        val timer = Timer(20, ClockMock())
        Assert.assertEquals("0.0", timer.text)
    }

    @Test
    fun timerStartedText() {
        val timer = Timer(1000, ClockMock())
        timer.start()
        Assert.assertEquals("1.0", timer.text)
    }

    @Test
    fun timerTextAfterSomeProgress() {
        val timer = Timer(1000, ClockMock(listOf(0, 200)))
        timer.start()
        timer.tick()
        Assert.assertEquals("0.8", timer.text)
    }

    @Test
    fun timerFinishedText() {
        val timer = Timer(1000, ClockMock(listOf(0, 1000)))
        timer.start()
        timer.tick()
        Assert.assertEquals("0.0", timer.text)
    }

    private class ClockMock(ticks: List<Long> = listOf(0)) : ISystemClock {
        val iter = ticks.listIterator()
        override fun elapsedRealtime(): Long {
            return iter.next()
        }
    }
}
