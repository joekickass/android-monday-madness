package com.joekickass.mondaymadness

import android.app.Application
import android.util.Log
import com.joekickass.mondaymadness.spotify.CLIENT_ID
import com.joekickass.mondaymadness.spotify.SpotifyFacade
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Spotify
import com.spotify.sdk.android.player.SpotifyPlayer

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
        if ("SpotifyService" == name) return spotify
        return super.getSystemService(name)
    }

    fun enableSpotify(token: String) {
        when (spotify.enabled) {
            true -> spotify.updatePlayer(token)
            false -> getNewPlayer(token)
        }
    }

    private fun getNewPlayer(token: String) {
        val playerConfig = Config(applicationContext, token, CLIENT_ID)
        Spotify.getPlayer(playerConfig, this, object : SpotifyPlayer.InitializationObserver {
            override fun onInitialized(player: SpotifyPlayer) {
                Log.d(TAG, "Spotify player initialized")
                spotify.setPlayer(player)
            }
            override fun onError(throwable: Throwable) {
                Log.e(TAG, "Could not initialize Spotify player: " + throwable.message)
                TODO()
            }
        })
    }

    override fun onTerminate() {
        super.onTerminate()
        spotify.unsetPlayer()
        Spotify.destroyPlayer(this)
    }

    companion object {
        private val TAG = MadnessApplication::class.java.simpleName
    }
}
