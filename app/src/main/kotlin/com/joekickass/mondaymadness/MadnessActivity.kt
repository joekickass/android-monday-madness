package com.joekickass.mondaymadness

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem

import com.joekickass.mondaymadness.menu.about.AboutActivity
import com.joekickass.mondaymadness.menu.interval.AddIntervalDialogFragment
import com.joekickass.mondaymadness.model.IntervalQueue
import com.joekickass.mondaymadness.model.Workout
import com.joekickass.mondaymadness.view.WorkoutView
import com.joekickass.mondaymadness.realm.Interval
import com.joekickass.mondaymadness.spotify.SpotifyFacade

import io.realm.Realm
import io.realm.RealmChangeListener
import io.realm.Sort

import kotlinx.android.synthetic.main.activity_main.*

/**
 * Inflates the [WorkoutView] and delegates adding a new interval to the [AddIntervalDialogFragment].
 * New intervals will be added to Realm, and [MadnessActivity] will be notified through
 * [RealmChangeListener].
 */
class MadnessActivity : AppCompatActivity(), RealmChangeListener<Realm> {

    private var spotify : SpotifyFacade? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        fab.setOnClickListener {
            pwv.click()
        }

        val realm = Realm.getDefaultInstance()
        realm.addChangeListener(this)

        spotify = application.getSystemService("SpotifyService") as SpotifyFacade

        //setNewInterval()
    }

    private fun setNewInterval() {
        val interval = lastInterval
        Log.d(TAG, "Setting new interval: w=" + interval.workInMillis +
                   " r=" + interval.restInMillis +
                   " reps=" + interval.repetitions)

        val w = Workout(IntervalQueue(
                interval.workInMillis,
                interval.restInMillis,
                interval.repetitions))

        w.onWorkRunning = { onWorkRunning() }
        w.onWorkPaused = { onWorkPaused() }
        w.onRestRunning = { onRestRunning() }
        w.onRestPaused = { onRestPaused() }
        w.onWorkoutFinished = { onFinished() }
        pwv.init(w)
    }

    override fun onDestroy() {
        super.onDestroy()
        val realm = Realm.getDefaultInstance()
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
                setStopState() // TODO: Need to reset spotify playlist?
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
                startActivity(Intent(this, AboutActivity::class.java))
                return true
            }

            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun onWorkRunning() {
        Log.d(TAG, "onWorkRunning")
        setWorkState()
    }

    private fun onWorkPaused() {
        Log.d(TAG, "onWorkPaused")
        setStopState()
    }

    private fun onRestRunning() {
        Log.d(TAG, "onRestRunning")
        setRestState()
    }

    private fun onRestPaused() {
        Log.d(TAG, "onRestPaused")
        setRestPausedState()
    }

    private fun onFinished() {
        Log.d(TAG, "onFinished")
        setStopState()
        setNewInterval()
    }

    private val lastInterval: Interval
        get() {
            val realm = Realm.getDefaultInstance()
            val result = realm.where(Interval::class.java).findAllSorted("timestamp", Sort.DESCENDING)
            return if (!result.isEmpty()) result.first() else Interval()
        }

    override fun onChange(realm: Realm) {
        Log.d(TAG, "Setting up new interval")
        setNewInterval()
    }

    // TODO: Need to rename these

    private fun setWorkState() {
        spotify?.play()
        fab.setImageResource(R.drawable.ic_pause_white_48dp)
    }

    private fun setStopState() {
        spotify?.stop()
        fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
    }

    private fun setRestState() {
        spotify?.stop()
        fab.setImageResource(R.drawable.ic_pause_white_48dp)
    }

    private fun setRestPausedState() {
        spotify?.stop()
        fab.setImageResource(R.drawable.ic_play_arrow_white_48dp)
    }

    companion object {
        private val TAG = MadnessActivity::class.java.simpleName
    }
}