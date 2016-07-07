package com.joekickass.mondaymadness.menu.interval

import android.app.Dialog
import android.app.DialogFragment
import android.content.DialogInterface
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.Log
import android.view.LayoutInflater
import android.view.View

import com.joekickass.mondaymadness.R
import com.joekickass.mondaymadness.model.Interval

import io.realm.Realm
import io.realm.RealmResults

/**
 * Allows the user to create a new interval

 * Only the latest interval will be available (might change in the future)
 */
class AddIntervalDialogFragment : DialogFragment() {

    private var mInitialWorkInMillis: Long = 0
    private var mInitialRestInMillis: Long = 0
    private var mInitialReps = 1

    private var mWorkMinPicker: AddIntervalPicker? = null
    private var mWorkSecPicker: AddIntervalPicker? = null
    private var mRestMinPicker: AddIntervalPicker? = null
    private var mRestSecPicker: AddIntervalPicker? = null
    private var mRepsPicker: AddIntervalPicker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mInitialWorkInMillis = arguments.getLong("work")
        mInitialRestInMillis = arguments.getLong("rest")
        mInitialReps = arguments.getInt("reps")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = inflateDialogView(activity.layoutInflater)
        return AlertDialog.Builder(activity).setTitle(R.string.add_interval).setView(dialog).setPositiveButton(android.R.string.ok
        ) { dialog, whichButton ->
            val work = toMillis(mWorkMinPicker!!.value, mWorkSecPicker!!.value)
            val rest = toMillis(mRestMinPicker!!.value, mRestSecPicker!!.value)
            val reps = mRepsPicker!!.value
            saveNewInterval(work, rest, reps)
        }.setNegativeButton(android.R.string.cancel, null).create()
    }

    private fun inflateDialogView(inflater: LayoutInflater): View {
        val dialog = inflater.inflate(R.layout.dialog_add_interval, null)
        mWorkMinPicker = dialog.findViewById(R.id.work_minute_picker) as AddIntervalPicker
        mWorkMinPicker!!.value = toMins(mInitialWorkInMillis).toInt()
        mWorkSecPicker = dialog.findViewById(R.id.work_second_picker) as AddIntervalPicker
        mWorkSecPicker!!.value = toSecs(mInitialWorkInMillis).toInt()
        mRestMinPicker = dialog.findViewById(R.id.rest_minute_picker) as AddIntervalPicker
        mRestMinPicker!!.value = toMins(mInitialRestInMillis).toInt()
        mRestSecPicker = dialog.findViewById(R.id.rest_second_picker) as AddIntervalPicker
        mRestSecPicker!!.value = toSecs(mInitialRestInMillis).toInt()
        mRepsPicker = dialog.findViewById(R.id.repetition_picker) as AddIntervalPicker
        mRepsPicker!!.value = mInitialReps
        return dialog
    }

    private fun saveNewInterval(work: Long, rest: Long, reps: Int) {

        // Update interval if it already exists
        val realm = Realm.getDefaultInstance()
        val identicalIntervals = realm.where(Interval::class.java).equalTo("workInMillis", work).equalTo("restInMillis", rest).equalTo("repetitions", reps).findAll()
        if (!identicalIntervals.isEmpty()) {
            val interval = identicalIntervals.first()
            realm.beginTransaction()
            interval.timestamp = System.currentTimeMillis()
            realm.commitTransaction()
            Log.d(Companion.TAG, "Updated interval")
            return
        }

        // Create new
        realm.beginTransaction()
        val interval = realm.createObject(Interval::class.java)
        interval.workInMillis = work
        interval.restInMillis = rest
        interval.repetitions = reps
        interval.timestamp = System.currentTimeMillis()
        realm.commitTransaction()
        Log.d(Companion.TAG, "New interval saved")
    }

    private fun toMillis(min: Int, sec: Int): Long {
        return ((min * 60 + sec) * 1000).toLong()
    }

    private fun toMins(millis: Long): Long {
        return millis / 1000 / 60
    }

    private fun toSecs(millis: Long): Long {
        return millis / 1000 % 60
    }

    companion object {

        private val TAG = AddIntervalDialogFragment::class.java.simpleName

        fun newInstance(initialWork: Long, initialRest: Long, initialReps: Int): AddIntervalDialogFragment {
            val bundle = Bundle()
            bundle.putLong("work", initialWork)
            bundle.putLong("rest", initialRest)
            bundle.putInt("reps", initialReps)
            val frag = AddIntervalDialogFragment()
            frag.arguments = bundle
            return frag
        }
    }
}