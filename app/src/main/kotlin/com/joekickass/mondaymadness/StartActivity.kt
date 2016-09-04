package com.joekickass.mondaymadness

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.joekickass.mondaymadness.spotify.CLIENT_ID
import com.joekickass.mondaymadness.spotify.REDIRECT_URI
import com.joekickass.mondaymadness.spotify.REQUEST_CODE
import com.joekickass.mondaymadness.spotify.SpotifyFacade
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.authentication.AuthenticationResponse
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.SpotifyPlayer
import com.spotify.sdk.android.player.Spotify

import kotlinx.android.synthetic.main.activity_start.*

class StartActivity : AppCompatActivity() {

    private var spotify : SpotifyFacade? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if already logged in
        spotify = application.getSystemService("SpotifyService") as SpotifyFacade?
        if (spotify?.enabled == true) {
            Log.d(TAG, "Already logged in, launching main activity")
            startApp()
            return
        }

        setContentView(R.layout.activity_start)

        gobtn.setOnClickListener {
            startSpotifyAuth()
        }
    }

    private fun startSpotifyAuth() {
        val request = AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(arrayOf("streaming"))
                .build()
        Log.d(TAG, "Starting LoginActivity")
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)

        Log.d(TAG, "LoginActivity finished, handling result...")
        if (requestCode == REQUEST_CODE) {

            val response = AuthenticationClient.getResponse(resultCode, intent)
            when (response.type) {
                AuthenticationResponse.Type.TOKEN -> handleLoginSuccess(response.accessToken)
                AuthenticationResponse.Type.ERROR -> handleLoginError(response.error)
                else -> handleLoginInterrupted()
            }
        }
    }

    private fun handleLoginSuccess(token: String) {
        Log.d(TAG, "Authenticated with Spotify, initializing player...")

        val playerConfig = Config(applicationContext, token, CLIENT_ID)
        Spotify.getPlayer(playerConfig, application, object : SpotifyPlayer.InitializationObserver {

            override fun onInitialized(player: SpotifyPlayer) {
                Log.d(TAG, "Spotify player initialized")
                val facade = application.getSystemService("SpotifyService") as SpotifyFacade
                facade.setPlayer(player)
                startApp()
            }

            override fun onError(throwable: Throwable) {
                Log.e(TAG, "Could not initialize Spotify player: " + throwable.message)
                TODO()
            }
        })
    }

    private fun handleLoginError(error: String) {
        Log.e(TAG, "Failed to authenticate with Spotify: " + error)
        Log.d(TAG, "Clearing cookies and retrying...")
        AuthenticationClient.clearCookies(this)
        startSpotifyAuth()
    }

    private fun handleLoginInterrupted() {
        Log.d(TAG, "Most likely auth flow cancelled or interrupted, try again...")
        startSpotifyAuth()
    }

    private fun startApp() {
        val intent = Intent(baseContext, MadnessActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private val TAG = StartActivity::class.java.simpleName
    }
}
