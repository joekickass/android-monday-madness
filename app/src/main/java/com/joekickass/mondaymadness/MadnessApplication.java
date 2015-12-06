package com.joekickass.mondaymadness;

import android.app.Application;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Only used to set default Realm config for now
 */
public class MadnessApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RealmConfiguration config = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(config);
    }
}
