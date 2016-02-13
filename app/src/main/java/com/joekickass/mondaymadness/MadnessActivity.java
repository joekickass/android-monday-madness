package com.joekickass.mondaymadness;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.joekickass.mondaymadness.menu.about.AboutActivity;
import com.joekickass.mondaymadness.menu.interval.AddIntervalDialogFragment;
import com.joekickass.mondaymadness.intervaltimer.IntervalTimer;
import com.joekickass.mondaymadness.intervaltimer.IntervalTimerView;
import com.joekickass.mondaymadness.model.Interval;
import com.joekickass.mondaymadness.spotify.SpotifyFacade;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

import static com.joekickass.mondaymadness.spotify.SpotifyConstants.CLIENT_ID;
import static com.joekickass.mondaymadness.spotify.SpotifyConstants.REDIRECT_URI;
import static com.joekickass.mondaymadness.spotify.SpotifyConstants.REQUEST_CODE;

/**
 * Main entry point for app
 *
 * Inflates the {@link IntervalTimerView} and connects it to its {@link IntervalTimer}
 * controller. Also delegates adding a new interval to the {@link AddIntervalDialogFragment}. New
 * intervals will be added to Realm, and {@link MadnessActivity} will be notified through
 * {@link RealmChangeListener#onChange()}.
 */
public class MadnessActivity extends AppCompatActivity implements IntervalTimer.IntervalTimerListener,
        RealmChangeListener {

    private static final String TAG = MadnessActivity.class.getSimpleName();

    private SpotifyFacade mFacade = null;

    private IntervalTimer mTimer;

    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID ,
                AuthenticationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();
        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setEnabled(false);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFacade.toggle();
                if (mTimer.isRunning()) {
                    mTimer.pause();
                    mFab.setImageResource(R.drawable.ic_play_arrow_white_48dp);
                } else {
                    mTimer.start();
                    mFab.setImageResource(R.drawable.ic_pause_white_48dp);
                }
            }
        });

        Realm realm = Realm.getInstance(getApplicationContext());
        realm.addChangeListener(this);

        setNewInterval();
        mTimer.addListener(this);
    }

    private void setNewInterval() {
        Interval interval = getLastInterval();
        final IntervalTimerView intervalTimerView = (IntervalTimerView) findViewById(R.id.pwv);
        mTimer = new IntervalTimer(intervalTimerView,
                interval.getWorkInMillis(),
                interval.getRestInMillis(),
                interval.getRepetitions());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Spotify.destroyPlayer(this);
        mTimer.removeListener(this);
        Realm realm = Realm.getInstance(getApplicationContext());
        realm.removeChangeListener(this);
        realm.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_reset:
                mFacade.stop();
                setNewInterval();
                return true;

            case R.id.action_add:
                Interval interval = getLastInterval();
                AddIntervalDialogFragment pickTime = AddIntervalDialogFragment.Companion.newInstance(
                        interval.getWorkInMillis(),
                        interval.getRestInMillis(),
                        interval.getRepetitions());
                pickTime.show(getFragmentManager(), "timepicker");
                return true;

            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRestFinished() {
        Log.d(TAG, "onRestFinished");
        mFacade.toggle();
    }

    @Override
    public void onWorkFinished() {
        Log.d(TAG, "onWorkFinished");
        mFacade.toggle();
    }

    @Override
    public void onIntervalTimerFinished() {
        Log.d(TAG, "onIntervalTimerFinished");
        mFacade.stop();
        setNewInterval();
    }

    private Interval getLastInterval() {
        Realm realm = Realm.getDefaultInstance();
        RealmResults<Interval> result = realm.where(Interval.class).findAllSorted("timestamp", Sort.DESCENDING);
        return !result.isEmpty() ? result.first() : new Interval();
    }

    @Override
    public void onChange() {
        Log.d(TAG, "Setting up new interval");
        setNewInterval();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {

                    @Override
                    public void onInitialized(Player player) {
                        Log.d(TAG, "Spotify initialized");
                        mFacade = new SpotifyFacade(player);
                        mFab.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e(TAG, "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }
}