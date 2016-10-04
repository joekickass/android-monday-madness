package com.joekickass.mondaymadness.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.joekickass.mondaymadness.R.styleable.WorkoutView
import com.joekickass.mondaymadness.R.styleable.*

import com.joekickass.mondaymadness.model.Workout

/**
 * A graphical visualization of a workout interval timer (countdown timer)
 *
 * To set a new interval, call [.init] with the desired interval time in ms.
 * To start the new interval, call [.start]. The view will handle countdown internally.
 *
 * Thanks to Antimonit for the idea behind this class.
 * http://stackoverflow.com/a/27293082
 */
class WorkoutView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mWorkout: Workout? = null

    // Internal
    private val mBackgroundPaint = Paint()
    private val mProgressPaint = Paint()
    private val mTextPaint = Paint()
    private val mCircleBounds = RectF()

    private var mTextOffset: Float = 0.toFloat()

    init {
        // Read attributes
        val a = context.theme.obtainStyledAttributes(attrs, WorkoutView, 0, 0)
        try {
            // the style of the background
            val bgColor = a.getColor(WorkoutView_color_background, 0)
            setPaintProperties(mBackgroundPaint, bgColor)

            // the style of the 'progress'
            val progressColor = a.getColor(WorkoutView_color_progress, 0)
            setPaintProperties(mProgressPaint, progressColor)

            // the style for the text in the middle
            val textColor = a.getColor(WorkoutView_color_text, 0)
            mTextOffset = setTextProperties(mTextPaint, textColor, RADIUS)

        } finally {
            a.recycle()
        }
    }

    fun init(workout: Workout) {
        mWorkout = workout
        postInvalidateOnAnimation()
    }

    fun start() {
        mWorkout?.start()
        postInvalidateOnAnimation()
    }

    fun pause() {
        mWorkout?.pause()
        postInvalidateOnAnimation()
    }

    fun click() {
        when {
            mWorkout?.running == true -> pause()
            else -> start()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerWidth = (canvas.width / 2).toFloat()
        val centerHeight = (canvas.height / 2).toFloat()

        fun drawCircle(text: String, paint: Paint) {
            canvas.drawCircle(centerWidth, centerHeight, RADIUS, paint)
            canvas.drawText(text, centerWidth, centerHeight + mTextOffset, mTextPaint)
        }

        // Since drawArc only draws clockwise, we need to start with the whole circle filled with
        // accent color, then paint it over with the background. It will look like it is the accent
        // color increasing, when it is in fact the background decreasing
        fun drawBackground(text: String) = drawCircle(text, mBackgroundPaint)
        fun drawForeground(text: String) = drawCircle(text, mProgressPaint)
        fun drawProgress(fraction: Double) {

            // Center the circle in the canvas
            mCircleBounds.set(centerWidth - RADIUS,
                    centerHeight - RADIUS,
                    centerWidth + RADIUS,
                    centerHeight + RADIUS)

            // Draw circle arc
            canvas.drawArc(mCircleBounds, // Size of progress circle
                        -90f, // -90 is at top
                        (fraction * 360).toFloat(), // [ 0 <= progress <= 1]
                        false,
                        mBackgroundPaint)

            // Draw nob on the circle arc
            canvas.drawCircle(
                    (centerWidth + Math.sin(fraction * 2.0 * Math.PI) * RADIUS).toFloat(),
                    (centerHeight - Math.cos(fraction * 2.0 * Math.PI) * RADIUS).toFloat(),
                    HANDLE_RADIUS.toFloat(),
                    mProgressPaint)
        }

        // Draw uninitialized, initialized and finished wiew/workout
        val workout = mWorkout
        if (workout == null) {
            drawBackground("0.0")
            return
        }

        val timer = workout.timer

        // Not yet started, show empty ring and total time
        if (timer.finished || timer.initialized) {
            drawBackground(timer.text)
            return
        }

        timer.tick()

        if (timer.finished) {
            postInvalidateOnAnimation()
            return
        }

        // draw progress circle
        drawForeground(timer.text)
        drawProgress(timer.fraction)

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