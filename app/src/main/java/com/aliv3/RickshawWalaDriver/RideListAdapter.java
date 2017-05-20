package com.aliv3.RickshawWalaDriver;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RideListAdapter extends ArrayAdapter {

    private Context context;

    public RideListAdapter(Context context, ArrayList<Ride> rides) {
        super(context, 0, rides);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Ride ride = (Ride) this.getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_list_item, parent, false);
        }

        // Lookup view for data population
        TextView clientName = (TextView) convertView.findViewById(R.id.customListItemClientName);
        TextView origin = (TextView) convertView.findViewById(R.id.customListItemOrigin);
        TextView destination = (TextView) convertView.findViewById(R.id.customListItemDestination);

        // Populate the data into the template view using the data object
        clientName.setText(ride.clientName);
        origin.setText(ride.originLat + ", " + ride.originLong);
        destination.setText(ride.destLat + ", " + ride.destLong);

        Button accept = (Button) convertView.findViewById(R.id.listBtnAccept);
        accept.setTag(ride.id);
        Button reject = (Button) convertView.findViewById(R.id.listBtnReject);

        final Ride rideFinal = ride;

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = String.valueOf(view.getTag());
                Helper.setPreference("current_ride_id", id);
                try {
                    Helper.postRideUpdate(id, "accepted", callback(rideFinal));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                Toast.makeText(getContext(), "Accepted " + view.getTag(), Toast.LENGTH_SHORT).show();
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Rejected", Toast.LENGTH_SHORT).show();
            }
        });

        // Return the completed view to render on screen
        return convertView;
    }

    private Callback callback(final Ride ride) {
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
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.has("success")) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle bundle = new Bundle();
                                    bundle.putDouble("destLat", ride.destLat);
                                    bundle.putDouble("destLong", ride.destLong);
                                    bundle.putDouble("originLat", ride.originLat);
                                    bundle.putDouble("originLong", ride.originLong);
                                    bundle.putDouble("fare", ride.fare);

                                    ConfirmRideFragment fragmentOperationConfirmRide = new ConfirmRideFragment();
                                    fragmentOperationConfirmRide.setArguments(bundle);
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_main, fragmentOperationConfirmRide);
                                    fragmentTransaction.commit();
                                }
                            });
                        } else if (jsonObject.has("error")) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, "Failed to update ride in the api", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(context, "Failed to update ride in the api - server error", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

}
