package com.joekickass.mondaymadness.spotify

import android.util.Log

import com.spotify.sdk.android.player.ConnectionStateCallback
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.PlayerNotificationCallback
import com.spotify.sdk.android.player.PlayerState


class SpotifyFacade : ConnectionStateCallback, PlayerNotificationCallback {

    private val TAG = SpotifyFacade::class.java.simpleName

    //Player is nullable (question mark)
    private var player: Player? = null

    private var lastKnownState: PlayerState? = null

    fun init(newPlayer: Player) {
        Log.d(TAG, "init")
        player = newPlayer
        //mPlayer is possibly null, but we know it's not. So we call it with "!!",
        // which can throw a NullPointerException
        player!!.addConnectionStateCallback(this)
        //...better yet, we just call it with "?" so nothing happens if it's null
        player?.addPlayerNotificationCallback(this)
    }

    private fun start() {
        Log.d(TAG, "start")
        player?.setShuffle(true)
        player?.play(PLAYLIST_URI)
    }

    fun toggle() {
        Log.d(TAG, "toggle")
        when {
            lastKnownState == null -> start() //We haven't started yet
            lastKnownState!!.playing -> pause() //We're started, so just toggle pause/resume
            else -> player?.resume()
        }
    }

    fun pause() {
        //TODO: Fade out
        player?.pause()
    }

    fun stop() {
        Log.d(TAG, "stop")
        player?.pause()
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

        const val CLIENT_ID = "0cf30ade6d4b413a96666654b674f147"

        const val REDIRECT_URI = "monday-madness://joekickass.com/callback"

        val REQUEST_CODE = 7221

        private val PLAYLIST_URI = "spotify:user:joekickass:playlist:7dS1phK4Dcb3EG5IalwD4x"
    }
}
