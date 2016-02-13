package com.joekickass.mondaymadness

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.joekickass.mondaymadness.menu.about.AboutActivity
import com.joekickass.mondaymadness.menu.interval.AddIntervalDialogFragment
import com.joekickass.mondaymadness.intervaltimer.IntervalTimer
import com.joekickass.mondaymadness.intervaltimer.IntervalTimerView
import com.joekickass.mondaymadness.model.Interval
import com.joekickass.mondaymadness.spotify.CLIENT_ID
import com.joekickass.mondaymadness.spotify.REDIRECT_URI
import com.joekickass.mondaymadness.spotify.REQUEST_CODE
import com.joekickass.mondaymadness.spotify.SpotifyFacade
import com.spotify.sdk.android.authentication.AuthenticationClient
import com.spotify.sdk.android.authentication.AuthenticationRequest
import com.spotify.sdk.android.player.Config
import com.spotify.sdk.android.player.Player
import com.spotify.sdk.android.player.Spotify

import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort

import com.spotify.sdk.android.authentication.AuthenticationResponse.Type.TOKEN

/**
 * Main entry point for app

 * Inflates the [IntervalTimerView] and connects it to its [IntervalTimer]
 * controller. Also delegates adding a new interval to the [AddIntervalDialogFragment]. New
 * intervals will be added to Realm, and [MadnessActivity] will be notified through
 * [RealmChangeListener].
 */
class MadnessActivity : AppCompatActivity(), IntervalTimer.IntervalTimerListener, RealmChangeListener {

    private var mFacade: SpotifyFacade? = null

    private var mTimer: IntervalTimer? = null

    private var mFab: FloatingActionButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val builder = AuthenticationRequest.Builder(CLIENT_ID, TOKEN, REDIRECT_URI)
        builder.setScopes(arrayOf("user-read-private", "streaming"))
        val request = builder.build()
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request)

        mFab = findViewById(R.id.fab) as FloatingActionButton
        mFab!!.isEnabled = false
        mFab!!.setOnClickListener {
            mFacade!!.toggle()
            if (mTimer!!.isRunning) {
                mTimer!!.pause()
                mFab!!.setImageResource(R.drawable.ic_play_arrow_white_48dp)
            } else {
                mTimer!!.start()
                mFab!!.setImageResource(R.drawable.ic_pause_white_48dp)
            }
        }

        val realm = Realm.getInstance(applicationContext)
        realm.addChangeListener(this)

        setNewInterval()
        mTimer!!.addListener(this)
    }

    private fun setNewInterval() {
        val interval = lastInterval
        val intervalTimerView = findViewById(R.id.pwv) as IntervalTimerView
        mTimer = IntervalTimer(intervalTimerView,
                interval.workInMillis,
                interval.restInMillis,
                interval.repetitions)
    }

    override fun onDestroy() {
        super.onDestroy()
        Spotify.destroyPlayer(this)
        mTimer!!.removeListener(this)
        val realm = Realm.getInstance(applicationContext)
        realm.removeChangeListener(this)
        realm.close()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_reset -> {
                mFacade!!.stop()
                setNewInterval()
                return true
            }

            R.id.action_add -> {
                val interval = lastInterval
                val pickTime = AddIntervalDialogFragment.newInstance(
                        interval.workInMillis,
                        interval.restInMillis,
                        interval.repetitions)
                pickTime.show(fragmentManager, "timepicker")
                return true
            }

            R.id.action_about -> {
                val intent = Intent(this, AboutActivity::class.java)
                startActivity(intent)
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onRestFinished() {
        Log.d(TAG, "onRestFinished")
        mFacade!!.toggle()
    }

    override fun onWorkFinished() {
        Log.d(TAG, "onWorkFinished")
        mFacade!!.toggle()
    }

    override fun onIntervalTimerFinished() {
        Log.d(TAG, "onIntervalTimerFinished")
        mFacade!!.stop()
        setNewInterval()
    }

    private val lastInterval: Interval
        get() {
            val realm = Realm.getDefaultInstance()
            val result = realm.where(Interval::class.java).findAllSorted("timestamp", Sort.DESCENDING)
            return if (!result.isEmpty()) result.first() else Interval()
        }

    override fun onChange() {
        Log.d(TAG, "Setting up new interval")
        setNewInterval()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)

        if (requestCode == REQUEST_CODE) {
            val response = AuthenticationClient.getResponse(resultCode, intent)
            if (response.type == TOKEN) {
                val playerConfig = Config(this, response.accessToken, CLIENT_ID)
                Spotify.getPlayer(playerConfig, this, object : Player.InitializationObserver {

                    override fun onInitialized(player: Player) {
                        Log.d(TAG, "Spotify initialized")
                        mFacade = SpotifyFacade(player)
                        mFab!!.isEnabled = true
                    }

                    override fun onError(throwable: Throwable) {
                        Log.e(TAG, "Could not initialize player: " + throwable.message)
                    }
                })
            }
        }
    }

    companion object {
        private val TAG = MadnessActivity::class.java.simpleName
    }
}