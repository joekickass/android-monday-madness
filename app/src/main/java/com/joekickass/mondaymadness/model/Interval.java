package com.joekickass.mondaymadness.model;

import io.realm.RealmObject;

/**
 * Simple domain object for Realm
 */
public class Interval extends RealmObject {

    private long workInMillis = 0;
    private long restInMillis = 0;
    private int repetitions = 1;
    private long timestamp;

    public long getWorkInMillis() { return workInMillis; }
    public void setWorkInMillis(long workInMillis) { this.workInMillis = workInMillis; }
    public long getRestInMillis() { return restInMillis; }
    public void setRestInMillis(long restInMillis) { this.restInMillis = restInMillis; }
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
