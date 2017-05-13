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

        if (authHeader != null && authHeader == "Bearer "+ accessToken) {
            accessToken = refreshToken();
            if(accessToken == null) { // if the refresh_token has expired
                accessToken = getNewToken();
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
            return null; // refresh token has expired
        }
    }

    private String getNewToken() throws IOException {
        String username = Helper.getPreference("username");
        String password = Helper.getPreference("username");

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
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response); // TODO need to handle api error, can't leave it like this

        String jsonResponse = response.body().string();
        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            Helper.setPreference("access_token", jsonObject.getString("access_token"));
            Helper.setPreference("refresh_token", jsonObject.getString("refresh_token"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Helper.getPreference("access_token");
    }
}
