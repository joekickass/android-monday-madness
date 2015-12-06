package com.joekickass.mondaymadness.spotify;

import android.util.Log;

import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

public class SpotifyFacade implements ConnectionStateCallback, PlayerNotificationCallback {

    private static final String TAG = SpotifyFacade.class.getSimpleName();

    public static final String CLIENT_ID = "0cf30ade6d4b413a96666654b674f147";

    public static final String REDIRECT_URI = "monday-madness://joekickass.com/callback";

    public static final int REQUEST_CODE = 7221;

    private static final String PLAYLIST_URI = "spotify:user:joekickass:playlist:7dS1phK4Dcb3EG5IalwD4x";

    private Player mPlayer;

    private PlayerState mLastKnownState;

    public void init(Player player) {
        Log.d(TAG, "init");
        mPlayer = player;
        mPlayer.addConnectionStateCallback(this);
        mPlayer.addPlayerNotificationCallback(this);
    }

    private void start() {
        Log.d(TAG, "start");
        if (mPlayer != null) {
            mPlayer.setShuffle(true);
            mPlayer.play(PLAYLIST_URI);
        }
    }

    public void toggle() {
        Log.d(TAG, "toggle");
        if (mPlayer != null) {
            if (mLastKnownState == null) {
                // We haven't started yet
                start();
            } else {
                // We're started, so just toggle pause/resume
                if (!mLastKnownState.playing) {
                    mPlayer.resume();
                } else {
                    mPlayer.pause();
                }
            }
        }
    }

    public void stop() {
        Log.d(TAG, "stop");
        mPlayer.pause();
        mLastKnownState = null;
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d(TAG, "onPlaybackEvent");
        mLastKnownState = playerState;
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String s) {
        Log.d(TAG, "onPlaybackError");
    }

    @Override
    public void onLoggedIn() {
        Log.d(TAG, "onLoggedIn");
    }

    @Override
    public void onLoggedOut() {
        Log.d(TAG, "onLoggedOut");
    }

    @Override
    public void onLoginFailed(Throwable throwable) {
        Log.d(TAG, "onLoginFailed");
    }

    @Override
    public void onTemporaryError() {
        Log.d(TAG, "onTemporaryError");
    }

    @Override
    public void onConnectionMessage(String s) {
        Log.d(TAG, "onConnectionMessage");
    }
}
