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

    val enabled: Boolean
        get() = player != null

    fun setPlayer(player: SpotifyPlayer) {
        Log.d(TAG, "init")
        // TODO: Set connectivity status
        player.addConnectionStateCallback(this)
        player.addNotificationCallback(this)
        this.player = player
    }

    fun updatePlayer(token: String) {
        player?.login(token)
    }

    fun unsetPlayer() {
        Log.d(TAG, "clear")
        player = null
    }

    fun play() {
        Log.d(TAG, "play")
        when {
            player?.metadata?.currentTrack == null -> player?.playUri(callback, PLAYLIST_URI, 0, 0)
            player?.playbackState?.isPlaying != true -> player?.resume(callback)
        }
    }

    fun stop() {
        Log.d(TAG, "stop")
        player?.pause(callback)
    }

    override fun onPlaybackEvent(event: PlayerEvent?) {
        Log.d(TAG, "onPlaybackEvent")
        // TODO: Keep track of metadata and playback state
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

    private val callback = object : Player.OperationCallback {
        override fun onSuccess() {
            Log.d(TAG, "OK!")
        }
        override fun onError(error: Error) {
            Log.d(TAG, "ERROR:" + error)
        }
    }

    companion object {
        // Temp playlist
        private const val PLAYLIST_URI = "spotify:user:joekickass:playlist:7dS1phK4Dcb3EG5IalwD4x"
    }
}

