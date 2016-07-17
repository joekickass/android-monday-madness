package com.joekickass.mondaymadness.spotify

import android.util.Log

import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerNotificationCallback
import com.spotify.sdk.android.player.PlayerState

/**
 * Spotify API facade.
 *
 * Simplifies interaction with [com.spotify.sdk.android.player.Player]
 */
class SpotifyFacade() : ConnectionStateCallback, PlayerNotificationCallback {

    private val TAG = SpotifyFacade::class.java.simpleName

    private var player : Player? = null

    val isEnabled : Boolean
        get() = player != null

    fun setPlayer(player: Player) {
        Log.d(TAG, "init")
        player.addConnectionStateCallback(this)
        player.addPlayerNotificationCallback(this)
        this.player = player
    }

    fun toggle() {
        Log.d(TAG, "toggle")
        player?.getPlayerState { state ->
            when {
                state.trackUri == null -> player?.play(PLAYLIST_URI)
                state.playing -> player?.pause()
                !state.playing -> player?.resume()
            }
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        player?.pause()
    }

    override fun onPlaybackEvent(eventType: PlayerNotificationCallback.EventType, playerState: PlayerState) {
        Log.d(TAG, "onPlaybackEvent")
    }

    override fun onPlaybackError(errorType: PlayerNotificationCallback.ErrorType, s: String) {
        Log.d(TAG, "onPlaybackError")
    }

    override fun onLoggedIn() {
        Log.d(TAG, "onLoggedIn")
    }

    override fun onLoggedOut() {
        Log.d(TAG, "onLoggedOut")
    }

    override fun onLoginFailed(throwable: Throwable) {
        Log.d(TAG, "onLoginFailed")
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

