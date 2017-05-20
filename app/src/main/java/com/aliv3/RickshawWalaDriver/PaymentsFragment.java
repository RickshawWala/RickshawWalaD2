package com.aliv3.RickshawWalaDriver;

import android.content.Context;
import android.content.Intent;
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

public class PaymentsFragment extends Fragment {

    Double fare;

    public PaymentsFragment () {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payments,container,false);

        fare = getArguments().getDouble("fare");

//        final ImageButton buttonCash = (ImageButton) rootView.findViewById(R.id.buttonCash);
//        final ImageButton buttonPaytm = (ImageButton) rootView.findViewById(R.id.buttonPaytm);
        final TextView getFareIs = (TextView) rootView.findViewById(R.id.getFareIs);
        final Button buttonConfirmPayment = (Button) rootView.findViewById(R.id.buttonConfirmPayment);

        /*buttonCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Pay Cash", Toast.LENGTH_SHORT).show();
            }
        });*/

        /*buttonPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Paytm", Toast.LENGTH_SHORT).show();

            }
        });*/

        getFareIs.setText("₹ " + fare.toString());

        buttonConfirmPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Helper.getPreference("current_ride_id");
                try {
                    Helper.postRideUpdate(id, "payment completed", callback());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //getFragmentManager().beginTransaction().remove(PaymentsFragment.this).commitAllowingStateLoss();
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
                                    Intent intent = new Intent(getActivity(), RideActivity.class);
                                    startActivity(intent);
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
