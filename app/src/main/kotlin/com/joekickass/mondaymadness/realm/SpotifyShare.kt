package com.joekickass.mondaymadness.realm

import io.realm.RealmObject

/**
 * Stores relevant stuff in Realm just for fun
 */
open class SpotifyShare : RealmObject() {
    open var url: String = ""
    open var timestamp: Long = 0
}

