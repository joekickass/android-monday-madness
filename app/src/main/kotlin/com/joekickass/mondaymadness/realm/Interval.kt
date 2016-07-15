package com.joekickass.mondaymadness.realm

import io.realm.RealmObject

/**
 * Stores relevant stuff in Realm just for fun
 */
open class Interval : RealmObject() {
    open var workInMillis: Long = 0
    open var restInMillis: Long = 0
    open var repetitions = 1
    open var timestamp: Long = 0
}
