package com.joekickass.mondaymadness

import android.app.Application
import com.joekickass.mondaymadness.spotify.SpotifyFacade
import com.spotify.sdk.android.player.Spotify

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Provides application wide access to external services (e.g. Spotify)
 *
 * Also sets default Realm config
 */
class MadnessApplication : Application() {

    private val spotify : SpotifyFacade = SpotifyFacade()

    override fun onCreate() {
        super.onCreate()
        val config = RealmConfiguration.Builder(this).build()
        Realm.setDefaultConfiguration(config)
    }

    override fun getSystemService(name: String?): Any? {
        if ("SpotifyService".equals(name)) return spotify
        return super.getSystemService(name)
    }

    override fun onTerminate() {
        super.onTerminate()
        Spotify.destroyPlayer(this)
    }
}
