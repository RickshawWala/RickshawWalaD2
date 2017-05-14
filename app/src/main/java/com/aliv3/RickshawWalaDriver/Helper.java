package com.aliv3.RickshawWalaDriver;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import okhttp3.OkHttpClient;

public class Helper {

    private static SharedPreferences mInstance = null;
    private static OkHttpClient mInstance2 = null;

    private static String host = "http://139.59.70.223";
    public static String POSTGetNewToken = host + "/api/auth/token";
    public static String POSTRefreshToken = host + "/api/auth/refresh";
    public static String POSTRegister = host + "/api/register";

    public static SharedPreferences getSharedPreferencesInstance() {
        if(mInstance == null) {
            mInstance = PreferenceManager.getDefaultSharedPreferences(RickshawWalaDriver.getAppContext());
        }
        return mInstance;
    }

    public static OkHttpClient getOkHttpClientInstance() {
        if(mInstance2 == null) {
            mInstance2 = new OkHttpClient();
        }
        return mInstance2;
    }

    public static void setPreference(String key, String value) {
        SharedPreferences.Editor editor = getSharedPreferencesInstance().edit();
        editor.putString(key, value);
        editor.apply();
    }
    public static String getPreference(String key) {
        return getSharedPreferencesInstance().getString(key, null);
    }

}
