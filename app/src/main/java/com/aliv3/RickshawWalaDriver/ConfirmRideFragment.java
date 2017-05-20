package com.aliv3.RickshawWalaDriver;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ConfirmRideFragment extends Fragment {

    Double destLat, destLong, originLat, originLong;
    Double fare;

    public ConfirmRideFragment () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_confirm_ride,container,false);

        final Button buttonStartRide = (Button) rootView.findViewById(R.id.buttonStartRide);
        originLat = getArguments().getDouble("originLat");
        originLong = getArguments().getDouble("originLong");
        destLat = getArguments().getDouble("destLat");
        destLong = getArguments().getDouble("destLong");
        fare = getArguments().getDouble("fare");

        TextView origin = (TextView) rootView.findViewById(R.id.textGetOrigin);
        origin.setText(originLat.toString() + ", " + originLong.toString());

        TextView destination = (TextView) rootView.findViewById(R.id.textGetDest);
        destination.setText(destLat.toString() + ", " + destLong.toString());

        TextView fareTxtView = (TextView) rootView.findViewById(R.id.textGetEstFare);
        fareTxtView.setText("₹ " + fare.toString());

        buttonStartRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Helper.getPreference("current_ride_id");
                try {
                    Helper.postRideUpdate(id, "started", callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        return rootView;
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
                    try {
                        JSONObject jsonObject = new JSONObject(jsonResponse);
                        if (jsonObject.has("success")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Bundle bundle = new Bundle();
                                    bundle.putDouble("originLat", originLat);
                                    bundle.putDouble("originLong", originLong);
                                    bundle.putDouble("destLat", destLat);
                                    bundle.putDouble("destLong", destLong);
                                    bundle.putDouble("fare", fare);

                                    MapsFragment fragmentOperationMaps = new MapsFragment();
                                    fragmentOperationMaps.setArguments(bundle);
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_main, fragmentOperationMaps);
                                    fragmentTransaction.commit();
                                }
                            });
                        } else if (jsonObject.has("error")) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getContext(), "Failed to update ride in the api", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to update ride in the api - server error", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
