package com.aliv3.RickshawWalaDriver;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;

public class RickshawWalaDriver extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();

        RickshawWalaDriver.context = getApplicationContext();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
    }

    public static Context getAppContext() {
        return RickshawWalaDriver.context;
    }

}
