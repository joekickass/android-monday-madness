package com.joekickass.mondaymadness.menu.interval;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.joekickass.mondaymadness.R;
import com.joekickass.mondaymadness.model.Interval;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Allows the user to create a new interval
 *
 * Only the latest interval will be available (might change in the future)
 */
public class AddIntervalDialogFragment extends DialogFragment {

    private static final String TAG = AddIntervalDialogFragment.class.getSimpleName();

    private long mInitialWorkInMillis = 0;
    private long mInitialRestInMillis = 0;
    private int mInitialReps = 1;

    private AddIntervalPicker mWorkMinPicker;
    private AddIntervalPicker mWorkSecPicker;
    private AddIntervalPicker mRestMinPicker;
    private AddIntervalPicker mRestSecPicker;
    private AddIntervalPicker mRepsPicker;

    public static AddIntervalDialogFragment newInstance(long initialWork, long initialRest, int initialReps) {
        Bundle bundle = new Bundle();
        bundle.putLong("work", initialWork);
        bundle.putLong("rest", initialRest);
        bundle.putInt("reps", initialReps);
        AddIntervalDialogFragment frag = new AddIntervalDialogFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInitialWorkInMillis = getArguments().getLong("work");
        mInitialRestInMillis = getArguments().getLong("rest");
        mInitialReps = getArguments().getInt("reps");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View dialog = inflateDialogView(getActivity().getLayoutInflater());
        return new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_interval)
                .setView(dialog)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                long work = toMillis(mWorkMinPicker.getValue(),
                                        mWorkSecPicker.getValue());
                                long rest = toMillis(mRestMinPicker.getValue(),
                                        mRestSecPicker.getValue());
                                int reps = mRepsPicker.getValue();
                                saveNewInterval(work, rest, reps);
                            }
                        }
                )
                .setNegativeButton(android.R.string.cancel, null)
                .create();
    }

    private View inflateDialogView(LayoutInflater inflater) {
        View dialog = inflater.inflate(R.layout.dialog_add_interval, null);
        mWorkMinPicker = (AddIntervalPicker) dialog.findViewById(R.id.work_minute_picker);
        mWorkMinPicker.setValue((int) toMins(mInitialWorkInMillis));
        mWorkSecPicker = (AddIntervalPicker) dialog.findViewById(R.id.work_second_picker);
        mWorkSecPicker.setValue((int) toSecs(mInitialWorkInMillis));
        mRestMinPicker = (AddIntervalPicker) dialog.findViewById(R.id.rest_minute_picker);
        mRestMinPicker.setValue((int) toMins(mInitialRestInMillis));
        mRestSecPicker = (AddIntervalPicker) dialog.findViewById(R.id.rest_second_picker);
        mRestSecPicker.setValue((int) toSecs(mInitialRestInMillis));
        mRepsPicker = (AddIntervalPicker) dialog.findViewById(R.id.repetition_picker);
        mRepsPicker.setValue(mInitialReps);
        return dialog;
    }

    private void saveNewInterval(final long work, final long rest, final int reps) {

        // Update interval if it already exists
        final Realm realm = Realm.getDefaultInstance();
        RealmResults<Interval> identicalIntervals = realm.where(Interval.class)
                .equalTo("workInMillis", work)
                .equalTo("restInMillis", rest)
                .equalTo("repetitions", reps)
                .findAll();
        if (!identicalIntervals.isEmpty()) {
            Interval interval = identicalIntervals.first();
            realm.beginTransaction();
            interval.setTimestamp(System.currentTimeMillis());
            realm.commitTransaction();
            Log.d(TAG, "Updated interval");
            return;
        }

        // Create new
        realm.beginTransaction();
        Interval interval = realm.createObject(Interval.class);
        interval.setWorkInMillis(work);
        interval.setRestInMillis(rest);
        interval.setRepetitions(reps);
        interval.setTimestamp(System.currentTimeMillis());
        realm.commitTransaction();
        Log.d(TAG, "New interval saved");
    }

    private long toMillis(int min, int sec) {
        return (min * 60 + sec) * 1000;
    }

    private long toMins(long millis) {
        return (millis / 1000) / 60;
    }

    private long toSecs(long millis) {
        return (millis / 1000) % 60;
    }
}