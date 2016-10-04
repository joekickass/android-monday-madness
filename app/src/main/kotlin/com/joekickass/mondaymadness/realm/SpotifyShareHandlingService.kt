package com.joekickass.mondaymadness.realm

import android.app.IntentService
import android.content.Intent
import android.util.Log
import io.realm.Realm

class SpotifyShareHandlingService : IntentService(TAG) {

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent: " + intent?.data)
        intent?.data?.let { saveShareUrl(it.toString()) }
    }

    private fun saveShareUrl(url: String) {

        Log.d(TAG, "Saving share: " + url)

        val realm = Realm.getDefaultInstance()

        val matches = realm.where(SpotifyShare::class.java).equalTo("url", url).findAll()
        when(matches.isEmpty()) {
            true -> {
                // Create new share if no existing found
                realm.beginTransaction()
                val share = realm.createObject(SpotifyShare::class.java)
                share.url = url
                share.timestamp = System.currentTimeMillis()
                realm.commitTransaction()
                Log.d(TAG, "New share saved")
            }
            false -> {
                // Update share if it already exists
                val share = matches.first()
                realm.beginTransaction()
                share.timestamp = System.currentTimeMillis()
                realm.commitTransaction()
                Log.d(TAG, "Updated share")
            }
        }
    }

    companion object {
        private val TAG = SpotifyShareHandlingService::class.java.simpleName
    }
}