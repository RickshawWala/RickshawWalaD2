package com.aliv3.RickshawWalaDriver;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.IOException;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Helper {

    private static SharedPreferences mInstance = null;
    private static OkHttpClient mInstance2 = null;

    private static String host = "http://139.59.70.223";
    private static String api = host + "/api";
    public static String POSTGetNewToken = api + "/auth/token";
    public static String POSTRefreshToken = api + "/auth/refresh";
    private static String POSTRegister = api + "/register";
    private static String GETUser = api + "/user";

    private static SharedPreferences getSharedPreferencesInstance() {
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

    public static void clearAllPreferences() {
        getSharedPreferencesInstance().edit().clear().commit();
    }

    public static void postRegisterUser(String name, final String email, String mobileNumber, final String password, Callback callback) throws IOException, IllegalArgumentException {
        OkHttpClient client = Helper.getOkHttpClientInstance().newBuilder().authenticator(new TokenAuthenticator()).build();

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("mobile_number", mobileNumber)
                .add("password", password)
                .add("is_user", "true")
                .build();
        Request request = new Request.Builder()
                .url(Helper.POSTRegister)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(callback);
    }

    public static void postRegisterDriver(String name, final String email, String mobileNumber, final String password, String licenseNumber, String registrationNumber, Callback callback) throws IOException, IllegalArgumentException {
        OkHttpClient client = Helper.getOkHttpClientInstance().newBuilder().authenticator(new TokenAuthenticator()).build();

        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("mobile_number", mobileNumber)
                .add("password", password)
                .add("is_driver", "true")
                .add("licence_number", licenseNumber)
                .add("vehicle_registration_number", registrationNumber)
                .build();
        Request request = new Request.Builder()
                .url(Helper.POSTRegister)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(callback);
    }

    public static void postGetToken(final String username, final String password, final String isType, Callback callback) throws IOException, IllegalArgumentException {
        OkHttpClient client = Helper.getOkHttpClientInstance();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .add(isType, "true")
                .build();
        Request request = new Request.Builder()
                .url(POSTGetNewToken)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(callback);
    }

    public static void GETUser(Callback callback) throws IOException, IllegalArgumentException {
        OkHttpClient client = Helper.getOkHttpClientInstance().newBuilder().authenticator(new TokenAuthenticator()).build();

        Request request = new Request.Builder()
                .url(GETUser)
                .addHeader("Accept", "application/json")
                .get()
                .build();

        client.newCall(request)
                .enqueue(callback);
    }

}