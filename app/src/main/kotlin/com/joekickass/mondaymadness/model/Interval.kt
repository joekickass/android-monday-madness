package com.joekickass.mondaymadness.model

import io.realm.RealmObject

/**
 * Simple domain object for Realm
 */
class Interval : RealmObject() {
    var workInMillis: Long = 0
    var restInMillis: Long = 0
    var repetitions = 1
    var timestamp: Long = 0
}
