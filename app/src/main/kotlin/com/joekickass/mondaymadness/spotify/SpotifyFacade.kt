package com.joekickass.mondaymadness.spotify

import android.util.Log
import com.spotify.sdk.android.player.*

/**
 * Spotify API facade.
 *
 * Simplifies interaction with [com.spotify.sdk.android.player.SpotifyPlayer]
 */
class SpotifyFacade() : ConnectionStateCallback, Player.NotificationCallback {

    private val TAG = SpotifyFacade::class.java.simpleName

    private var player : Player? = null

    val isEnabled : Boolean
        get() = player != null

    fun setPlayer(player: SpotifyPlayer) {
        Log.d(TAG, "init")
        player.addConnectionStateCallback(this)
        player.addNotificationCallback(this)
        this.player = player
    }

    fun play() {
        Log.d(TAG, "play")
        when {
            player?.playbackState?.isPlaying as Boolean -> {} // do nothing
            player?.playbackState?.isPlaying != true -> player?.resume()
            player?.metadata == null -> player?.playUri(PLAYLIST_URI, 0, 0)
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        player?.pause()
    }

    override fun onPlaybackEvent(event: PlayerEvent?) {
        Log.d(TAG, "onPlaybackEvent")
    }

    override fun onLoggedIn() {
        Log.d(TAG, "onLoggedIn")
    }

    override fun onLoggedOut() {
        Log.d(TAG, "onLoggedOut")
    }

    override fun onLoginFailed(error: Int) {
        Log.d(TAG, "onLoginFailed")
    }

    override fun onPlaybackError(error: Error?) {
        Log.d(TAG, "onPlaybackError")
    }

    override fun onTemporaryError() {
        Log.d(TAG, "onTemporaryError")
    }

    override fun onConnectionMessage(s: String) {
        Log.d(TAG, "onConnectionMessage")
    }

    companion object {
        // Temp playlist
        private const val PLAYLIST_URI = "spotify:user:joekickass:playlist:7dS1phK4Dcb3EG5IalwD4x"
    }
}

