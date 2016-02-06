package com.joekickass.mondaymadness

import android.app.Application

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Only used to set default Realm config for now
 */
class MadnessApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(this).build()
        Realm.setDefaultConfiguration(config)
    }
}
