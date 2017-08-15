package com.iszumi.movielover;

import android.app.Application;

import io.realm.Realm;

/**
 * Thanks to my sensei: @hendrawd
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}
