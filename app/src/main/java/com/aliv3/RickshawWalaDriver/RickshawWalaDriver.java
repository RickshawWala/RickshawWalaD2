package com.aliv3.RickshawWalaDriver;

import android.app.Application;
import android.content.Context;

public class RickshawWalaDriver extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        RickshawWalaDriver.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return RickshawWalaDriver.context;
    }

}
