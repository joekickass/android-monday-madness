package com.joekickass.mondaymadness.intervaltimer;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import static com.joekickass.mondaymadness.R.styleable.*;

/**
 * A graphical visualization of a interval timer (countdown timer)
 *
 * To set a new interval, call {@link #init(long)} with the desired interval time in ms.
 * To start the new interval, call {@link #start()}. The view will handle countdown internally.
 *
 * When a interval is finished, the {@link Callback#onIntervalFinished()} callback is invoked.
 *
 * Thanks to Antimonit for the idea behind this class.
 * http://stackoverflow.com/a/27293082
 */
public class IntervalTimerView extends View {

    public interface Callback {
        void onIntervalFinished();
    }

    private enum State { FINISHED, INITIALIZED, RUNNING, PAUSED };

    // TODO: Make relative view size?
    private static final int HANDLE_RADIUS = 5;

    // TODO: Make relative view size?
    private static final float RADIUS = 300;

    // Input params
    private long mTotalInMillis;

    // Internal
    private final Paint mBackgroundPaint = new Paint();
    private final Paint mProgressPaint = new Paint();
    private final Paint mTextPaint = new Paint();
    private final RectF mCircleBounds = new RectF();

    private float mTextOffset;
    private State mState;
    private long mStartTimeInMillis;
    private long mTimeLeftInMillis;

    private Callback mCallback = EMPTY_CALLBACK;

    public IntervalTimerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // Read attributes
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, IntervalTimerView, 0, 0);
        try {

            // the style of the background
            int bgColor = a.getColor(IntervalTimerView_color_background, 0);
            setPaintProperties(mBackgroundPaint, bgColor);

            // the style of the 'progress'
            int progressColor = a.getColor(IntervalTimerView_color_progress, 0);
            setPaintProperties(mProgressPaint, progressColor);

            // the style for the text in the middle
            int textColor = a.getColor(IntervalTimerView_color_text, 0);
            mTextOffset = setTextProperties(mTextPaint, textColor, RADIUS);

        } finally {
            a.recycle();
        }

        // start in finished state (no work time set)
        finish();
    }

    public void registerCallback(Callback callback) {
        mCallback = callback;
    }

    public void unregisterCallback() {
        mCallback = EMPTY_CALLBACK;
    }

    public void init(long workInMillis) {
        mStartTimeInMillis = mTimeLeftInMillis = 0;
        mTotalInMillis = workInMillis;
        mState = State.INITIALIZED;
        postInvalidateOnAnimation();
    }

    public void start() {
        if (isFinished()) {
            throw new IllegalStateException("Must (re)initialize view before starting");
        }
        mStartTimeInMillis = SystemClock.elapsedRealtime();
        // If we were paused, we need to subtract time already spent in this interval
        mStartTimeInMillis -= isPaused() ? mTotalInMillis - mTimeLeftInMillis : 0;
        mState = State.RUNNING;
        postInvalidateOnAnimation();
    }

    public void pause() {
        if (isFinished()) {
            throw new IllegalStateException("Must (re)initialize view before pausing");
        }
        mStartTimeInMillis = 0;
        mState = State.PAUSED;
        postInvalidateOnAnimation();
    }

    public void finish() {
        mStartTimeInMillis = mTimeLeftInMillis = mTotalInMillis = 0;
        mState = State.FINISHED;
        postInvalidateOnAnimation();
        mCallback.onIntervalFinished();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerWidth = canvas.getWidth() / 2;
        float centerHeight = canvas.getHeight() / 2;

        // Center the circle in the canvas
        mCircleBounds.set(centerWidth - RADIUS,
                centerHeight - RADIUS,
                centerWidth + RADIUS,
                centerHeight + RADIUS);

        // Not yet started, show empty ring and total time (can be '0.0' if finished)
        if (isFinished() || isInitialized()) {
            String value = Double.toString(((double)(mTotalInMillis/100)/10));
            canvas.drawCircle(centerWidth, centerHeight, RADIUS, mBackgroundPaint);
            canvas.drawText(value, centerWidth, centerHeight + mTextOffset, mTextPaint);
            return;
        }

        // Calculate new progress only if we're running, else keep the old value...
        if (isRunning()) {
            mTimeLeftInMillis = mTotalInMillis - (SystemClock.elapsedRealtime() - mStartTimeInMillis);
        }

        // Finish if we reached 0
        if (mTimeLeftInMillis <= 0) {
            finish();
            return;
        }

        // Since drawArc only draws clockwise, we need to start with the whole circle filled with
        // accent color, then paint it over with the background. It will look like it is the accent
        // color increasing, when it is in fact the background decreasing.
        canvas.drawCircle(centerWidth, centerHeight, RADIUS, mProgressPaint);

        // Display text inside the circle
        canvas.drawText(
                Double.toString(((double)(mTimeLeftInMillis /100)/10)),
                centerWidth,
                centerHeight + mTextOffset,
                mTextPaint);

        // Draw progress circle
        double timeLeft = (double) mTimeLeftInMillis / mTotalInMillis;

        // Decrease accent color circle by painting it over with background color.
        canvas.drawArc(
                mCircleBounds,               // Size of progress circle
                -90,                        // -90 is at top
                (float)(timeLeft*360),      // [ 0 <= progress <= 1]
                false,
                mBackgroundPaint);

        // Draw nob on the circle
        canvas.drawCircle((float)(centerWidth  + (Math.sin(timeLeft * 2 * Math.PI) * RADIUS)),
                (float)(centerHeight - (Math.cos(timeLeft * 2 * Math.PI) * RADIUS)),
                HANDLE_RADIUS, mProgressPaint);

        // make sure we continue draw ourselves until time expires
        postInvalidateOnAnimation();
    }

    public boolean isRunning() {
        return mState == State.RUNNING;
    }

    public boolean isPaused() {
        return mState == State.PAUSED;
    }

    public boolean isInitialized() {
        return mState == State.INITIALIZED;
    }

    public boolean isFinished() {
        return mState == State.FINISHED;
    }

    private void setPaintProperties(Paint paint, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setColor(color);
    }

    private float setTextProperties(Paint paint, int color, float radius) {
        paint.setTextSize(radius / 2);
        paint.setColor(color);
        paint.setTextAlign(Paint.Align.CENTER);
        float textHeight = paint.descent() - paint.ascent();
        return (textHeight / 2) - paint.descent();
    }

    private static final Callback EMPTY_CALLBACK = new Callback() {
        public void onIntervalFinished() {}
    };
}