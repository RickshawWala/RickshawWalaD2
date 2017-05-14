package com.aliv3.RickshawWalaDriver;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import timber.log.Timber;

public class TokenAuthenticator implements Authenticator {
    /**
     * This class handles three cases to authenticate
     * 1. use the access_token in the preference
     * 2. if access_token in the preference has expired use refresh_token from the preference
     * 3. if refresh_token in the preference has also expired, get new tokens using the username & password
     */
    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        String accessToken = Helper.getPreference("access_token");

        String authHeader = response.request().header("Authorization");

        if (authHeader != null) {
            accessToken = refreshToken();
            Timber.d("TokenAuthenticator", "refreshToken");
            if(accessToken == null) {  // refresh token has expired
                Timber.d("TokenAuthenticator", "getNewToken");
                accessToken = getNewToken();
                if(accessToken == null) { // invalid username & password
                    return null; // give up authentication
                }
            }
        }

        return response.request().newBuilder()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .build();
    }

    private String refreshToken() throws IOException {
        String refreshToken = Helper.getPreference("refresh_token");
        OkHttpClient client = Helper.getOkHttpClientInstance();

        RequestBody formBody = new FormBody.Builder()
                .add("refresh_token", refreshToken)
                .build();
        Request request = new Request.Builder()
                .url(Helper.POSTRefreshToken)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String jsonResponse = response.body().string();
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                Helper.setPreference("access_token", jsonObject.getString("access_token"));
                Helper.setPreference("refresh_token", jsonObject.getString("refresh_token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Helper.getPreference("access_token");
        } else {
            return null; // refresh token has expired - or - the server is down
        }
    }

    private String getNewToken() throws IOException {
        String username = Helper.getPreference("username");
        String password = Helper.getPreference("password");

        OkHttpClient client = Helper.getOkHttpClientInstance();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url(Helper.POSTGetNewToken)
                .post(formBody)
                .build();

        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            String jsonResponse = response.body().string();
            try {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                Helper.setPreference("access_token", jsonObject.getString("access_token"));
                Helper.setPreference("refresh_token", jsonObject.getString("refresh_token"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return Helper.getPreference("access_token");
        } else {
            return null; // invalid username & password - or - the server is down
        }
    }
}
