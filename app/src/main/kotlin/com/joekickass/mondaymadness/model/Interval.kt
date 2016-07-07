package com.joekickass.mondaymadness.model

import io.realm.RealmObject

/**
 * Represents an interval session
 */
open class Interval : RealmObject() {
    open var workInMillis: Long = 0
    open var restInMillis: Long = 0
    open var repetitions = 1
    open var timestamp: Long = 0
}
