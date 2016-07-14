package com.joekickass.mondaymadness.model

import io.realm.RealmObject

/**
 * Represents one interval
 */
open class Interval : RealmObject() {
    open var workInMillis: Long = 0
    open var restInMillis: Long = 0
    open var repetitions = 1
    open var timestamp: Long = 0
}
