package com.aliv3.RickshawWalaDriver;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RideActivity extends AppCompatActivity {

    boolean doubleBackToExitPressedOnce = false;
    RideListAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride);

        ArrayList<Ride> arrayOfRides = new ArrayList<Ride>();
        adapter = new RideListAdapter(this, arrayOfRides);
        listView = (ListView) findViewById(R.id.rideListView);
        listView.setAdapter(adapter);

        new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                //Log.d("Timer", "3, 2, 1");
                try {
                    Helper.getCreatedRides(callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 10*1000);
    }

    private Callback callback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("RideActivity", e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String jsonResponse = response.body().string();
                    Log.d("JSON", jsonResponse);
                    clearAdapter();
                    try {
                        final JSONArray jsonArray = new JSONArray(jsonResponse);
                        for(int i = 0; i < jsonArray.length(); i++) {
                            Integer id = jsonArray.getJSONObject(i).getInt("id");
                            double origLat = jsonArray.getJSONObject(i).getDouble("origin_latitude");
                            double origLong = jsonArray.getJSONObject(i).getDouble("origin_longitude");
                            double destLat = jsonArray.getJSONObject(i).getDouble("destination_latitude");
                            double destLong = jsonArray.getJSONObject(i).getDouble("destination_longitude");
                            double fare = jsonArray.getJSONObject(i).getDouble("fare");
                            String clientName = jsonArray.getJSONObject(i).getJSONObject("client").getString("name");
                            Ride newRide = new Ride(id, clientName, origLat, origLong, destLat, destLong, fare);
                            addRide(newRide);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d("RideActivity", "Failed to get the created rides from the api");
                }
            }
        };
    }

    private void clearAdapter() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.clear();
            }
        });
    }

    private void addRide(final Ride newRide) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.add(newRide);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id) {
            case R.id.myprofile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            case R.id.settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            finish();
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
