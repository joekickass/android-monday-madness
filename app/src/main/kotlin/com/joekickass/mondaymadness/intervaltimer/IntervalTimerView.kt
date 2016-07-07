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
import com.joekickass.mondaymadness.model.Timer

/**
 * A graphical visualization of a interval timer (countdown timer)

 * To set a new interval, call [.init] with the desired interval time in ms.
 * To start the new interval, call [.start]. The view will handle countdown internally.

 * When a interval is finished, the [Callback.onIntervalFinished] callback is invoked.

 * Thanks to Antimonit for the idea behind this class.
 * http://stackoverflow.com/a/27293082
 */
class IntervalTimerView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mTimer = Timer(0)

    // Internal
    private val mBackgroundPaint = Paint()
    private val mProgressPaint = Paint()
    private val mTextPaint = Paint()
    private val mCircleBounds = RectF()

    // Callback function
    private var mNotifyFinished : () -> Unit = {}

    private var mTextOffset: Float = 0.toFloat()

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
    }

    fun init(timer: Timer, notifyFinished : () -> Unit) {
        mTimer = timer
        mNotifyFinished = notifyFinished
        postInvalidateOnAnimation()
    }

    fun start() {
        mTimer.start()
        postInvalidateOnAnimation()
    }

    fun pause() {
        mTimer.pause()
        postInvalidateOnAnimation()
    }

    fun finish() {
        mTimer.finish()
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
        if (mTimer.isFinished || mTimer.isInitialized) {

            canvas.drawCircle(centerWidth, centerHeight, RADIUS, mBackgroundPaint)
            canvas.drawText(
                    mTimer.text,
                    centerWidth,
                    centerHeight + mTextOffset,
                    mTextPaint)
            return
        }

        mTimer.tick()

        // Since drawArc only draws clockwise, we need to start with the whole circle filled with
        // accent color, then paint it over with the background. It will look like it is the accent
        // color increasing, when it is in fact the background decreasing.
        canvas.drawCircle(centerWidth, centerHeight, RADIUS, mProgressPaint)

        // Display text inside the circle
        canvas.drawText(
                mTimer.text,
                centerWidth,
                centerHeight + mTextOffset,
                mTextPaint)

        // Draw progress circle
        // Decrease accent color circle by painting it over with background color.
        canvas.drawArc(
                mCircleBounds, // Size of progress circle
                -90f, // -90 is at top
                (mTimer.fraction * 360).toFloat(), // [ 0 <= progress <= 1]
                false,
                mBackgroundPaint)

        // Draw nob on the circle
        canvas.drawCircle(
                (centerWidth + Math.sin(mTimer.fraction * 2.0 * Math.PI) * RADIUS).toFloat(),
                (centerHeight - Math.cos(mTimer.fraction * 2.0 * Math.PI) * RADIUS).toFloat(),
                HANDLE_RADIUS.toFloat(),
                mProgressPaint)

        // make sure we continue draw ourselves until time expires
        postInvalidateOnAnimation()
    }

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