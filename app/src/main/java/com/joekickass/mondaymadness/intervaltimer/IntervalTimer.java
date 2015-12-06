package com.joekickass.mondaymadness.intervaltimer;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Controller for {@link IntervalTimerView}
 *
 * Handles two types of intervals; WORK and REST. It is also possible to create a chain of intervals
 * by specifying the number of repetitions.
 */
public class IntervalTimer implements IntervalTimerView.Callback {

    private final long mWorkInMillis;

    private final long mRestInMillis;

    private final int mRepetitions;

    public interface IntervalTimerListener {
        void onWorkFinished();
        void onRestFinished();
        void onIntervalTimerFinished();
    }

    private final IntervalTimerView mView;

    private Queue<Interval> mWorkQueue;

    private Interval mCurrent;

    // TODO: Create special notifier/listener class with thread handling?
    private List<IntervalTimerListener> mListeners = new CopyOnWriteArrayList<>();

    public IntervalTimer(IntervalTimerView view, long workInMillis, long restInMillis, int repetitions) {
        mWorkInMillis = workInMillis;
        mRestInMillis = restInMillis;
        mRepetitions = repetitions;
        mView = view;
        mView.registerCallback(this);
        reset();
    }

    public void start() {
        mView.start();
    }

    public void pause() {
        mView.pause();
    }

    public boolean isRunning() {
        return mView.isRunning();
    }

    public void reset() {
        mWorkQueue = generateWorkQueue(mWorkInMillis, mRestInMillis, mRepetitions);
        mCurrent = mWorkQueue.poll();
        mView.init(mCurrent.time);
    }

    @Override
    public void onIntervalFinished() {

        // Notify listeners
        for (IntervalTimerListener listener : mListeners) {
            switch (mCurrent.type) {
                case WORK:
                    listener.onWorkFinished();
                    break;
                case REST:
                    listener.onRestFinished();
                    break;
            }
        }

        // Start new if there are any intervals left
        if (!mWorkQueue.isEmpty()) {
            mCurrent = mWorkQueue.poll();
            mView.init(mCurrent.time);
            mView.start();
        } else {
            for (IntervalTimerListener listener : mListeners) {
                listener.onIntervalTimerFinished();
            }
        }
    }

    public void addListener(IntervalTimerListener listener) {
        mListeners.add(listener);
    }

    public void removeListener(IntervalTimerListener listener) {
        mListeners.remove(listener);
    }

    private Queue<Interval> generateWorkQueue(long work, long rest, int repetitions) {
        Queue<Interval> ret = new LinkedList<>();
        for (int i = 1; i <= repetitions; ++i) {
            ret.add(new Interval(Interval.IntervalType.WORK, work));
            ret.add(new Interval(Interval.IntervalType.REST, rest));
        }
        return ret;
    }

    private static class Interval {
        private enum IntervalType { WORK, REST; }
        Interval(IntervalType type, long time) {
            this.type = type;
            this.time = time;
        }
        IntervalType type;
        long time;
    }
}