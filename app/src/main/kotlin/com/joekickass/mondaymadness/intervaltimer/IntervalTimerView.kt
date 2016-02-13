package com.joekickass.mondaymadness.intervaltimer

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.os.SystemClock
import android.util.AttributeSet
import android.view.View

import com.joekickass.mondaymadness.R.styleable.IntervalTimerView
import com.joekickass.mondaymadness.R.styleable.*

/**
 * A graphical visualization of a interval timer (countdown timer)

 * To set a new interval, call [.init] with the desired interval time in ms.
 * To start the new interval, call [.start]. The view will handle countdown internally.

 * When a interval is finished, the [Callback.onIntervalFinished] callback is invoked.

 * Thanks to Antimonit for the idea behind this class.
 * http://stackoverflow.com/a/27293082
 */
class IntervalTimerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private enum class State {
        FINISHED, INITIALIZED, RUNNING, PAUSED
    }

    // Internal
    private val mBackgroundPaint = Paint()
    private val mProgressPaint = Paint()
    private val mTextPaint = Paint()
    private val mCircleBounds = RectF()

    // Input params
    private var mTotalInMillis: Long = 0

    // Callback function
    private var mNotifyFinished : () -> Unit = {}

    private var mTextOffset: Float = 0.toFloat()
    private var mState: State? = null
    private var mStartTimeInMillis: Long = 0
    private var mTimeLeftInMillis: Long = 0

    init {

        // Read attributes
        val a = context.theme.obtainStyledAttributes(attrs, IntervalTimerView, 0, 0)
        try {

            // the style of the background
            val bgColor = a.getColor(IntervalTimerView_color_background, 0)
            setPaintProperties(mBackgroundPaint, bgColor)

            // the style of the 'progress'
            val progressColor = a.getColor(IntervalTimerView_color_progress, 0)
            setPaintProperties(mProgressPaint, progressColor)

            // the style for the text in the middle
            val textColor = a.getColor(IntervalTimerView_color_text, 0)
            mTextOffset = setTextProperties(mTextPaint, textColor, RADIUS)

        } finally {
            a.recycle()
        }

        // start in finished state (no work time set)
        finish()
    }

    fun init(workInMillis: Long, notifyFinished : () -> Unit) {
        mStartTimeInMillis = 0
        mTimeLeftInMillis = 0
        mTotalInMillis = workInMillis
        mNotifyFinished = notifyFinished
        mState = State.INITIALIZED
        postInvalidateOnAnimation()
    }

    fun start() {
        if (isFinished) {
            throw IllegalStateException("Must (re)initialize view before starting")
        }
        mStartTimeInMillis = SystemClock.elapsedRealtime()
        // If we were paused, we need to subtract time already spent in this interval
        mStartTimeInMillis -= if (isPaused) mTotalInMillis - mTimeLeftInMillis else 0
        mState = State.RUNNING
        postInvalidateOnAnimation()
    }

    fun pause() {
        if (isFinished) {
            throw IllegalStateException("Must (re)initialize view before pausing")
        }
        mStartTimeInMillis = 0
        mState = State.PAUSED
        postInvalidateOnAnimation()
    }

    fun finish() {
        mStartTimeInMillis = 0
        mTimeLeftInMillis = 0
        mTotalInMillis = 0
        mState = State.FINISHED
        postInvalidateOnAnimation()
        mNotifyFinished()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerWidth = (canvas.width / 2).toFloat()
        val centerHeight = (canvas.height / 2).toFloat()

        // Center the circle in the canvas
        mCircleBounds.set(centerWidth - RADIUS,
                centerHeight - RADIUS,
                centerWidth + RADIUS,
                centerHeight + RADIUS)

        // Not yet started, show empty ring and total time (can be '0.0' if finished)
        if (isFinished || isInitialized) {
            val value = java.lang.Double.toString((mTotalInMillis / 100).toDouble() / 10)
            canvas.drawCircle(centerWidth, centerHeight, RADIUS, mBackgroundPaint)
            canvas.drawText(value, centerWidth, centerHeight + mTextOffset, mTextPaint)
            return
        }

        // Calculate new progress only if we're running, else keep the old value...
        if (isRunning) {
            mTimeLeftInMillis = mTotalInMillis - (SystemClock.elapsedRealtime() - mStartTimeInMillis)
        }

        // Finish if we reached 0
        if (mTimeLeftInMillis <= 0) {
            finish()
            return
        }

        // Since drawArc only draws clockwise, we need to start with the whole circle filled with
        // accent color, then paint it over with the background. It will look like it is the accent
        // color increasing, when it is in fact the background decreasing.
        canvas.drawCircle(centerWidth, centerHeight, RADIUS, mProgressPaint)

        // Display text inside the circle
        canvas.drawText(
                java.lang.Double.toString((mTimeLeftInMillis / 100).toDouble() / 10),
                centerWidth,
                centerHeight + mTextOffset,
                mTextPaint)

        // Draw progress circle
        val timeLeft = mTimeLeftInMillis.toDouble() / mTotalInMillis

        // Decrease accent color circle by painting it over with background color.
        canvas.drawArc(
                mCircleBounds, // Size of progress circle
                -90f, // -90 is at top
                (timeLeft * 360).toFloat(), // [ 0 <= progress <= 1]
                false,
                mBackgroundPaint)

        // Draw nob on the circle
        canvas.drawCircle((centerWidth + Math.sin(timeLeft * 2.0 * Math.PI) * RADIUS).toFloat(),
                (centerHeight - Math.cos(timeLeft * 2.0 * Math.PI) * RADIUS).toFloat(),
                HANDLE_RADIUS.toFloat(), mProgressPaint)

        // make sure we continue draw ourselves until time expires
        postInvalidateOnAnimation()
    }

    val isRunning: Boolean
        get() = mState == State.RUNNING

    val isPaused: Boolean
        get() = mState == State.PAUSED

    val isInitialized: Boolean
        get() = mState == State.INITIALIZED

    val isFinished: Boolean
        get() = mState == State.FINISHED

    private fun setPaintProperties(paint: Paint, color: Int) {
        paint.style = Paint.Style.STROKE
        paint.isAntiAlias = true
        paint.strokeWidth = 10f
        paint.strokeCap = Paint.Cap.SQUARE
        paint.color = color
    }

    private fun setTextProperties(paint: Paint, color: Int, radius: Float): Float {
        paint.textSize = radius / 2
        paint.color = color
        paint.textAlign = Paint.Align.CENTER
        val textHeight = paint.descent() - paint.ascent()
        return textHeight / 2 - paint.descent()
    }

    companion object {
        // TODO: Make relative view size?
        private val HANDLE_RADIUS = 5
        // TODO: Make relative view size?
        private val RADIUS = 300f
    }
}