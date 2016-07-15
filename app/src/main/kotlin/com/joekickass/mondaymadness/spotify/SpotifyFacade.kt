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
class SpotifyFacade(private val player : Player) : ConnectionStateCallback, PlayerNotificationCallback {

    private val TAG = SpotifyFacade::class.java.simpleName

    private var lastKnownState: PlayerState? = null

    init {
        Log.d(TAG, "init")
        player.addConnectionStateCallback(this)
        player.addPlayerNotificationCallback(this)
    }

    fun toggle() {
        Log.d(TAG, "toggle")
        when {
            // TODO: Start as well
            lastKnownState == null -> player.getPlayerState { state -> lastKnownState = state }
            lastKnownState!!.playing -> player.pause()
            else -> player.resume()
        }
    }

    private fun start() {
        Log.d(TAG, "start")
        player.play(PLAYLIST_URI)
        player.setShuffle(true)
    }

    fun stop() {
        Log.d(TAG, "stop")
        player.pause()
        lastKnownState = null
    }

    override fun onPlaybackEvent(eventType: PlayerNotificationCallback.EventType, playerState: PlayerState) {
        Log.d(TAG, "onPlaybackEvent")
        lastKnownState = playerState
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

