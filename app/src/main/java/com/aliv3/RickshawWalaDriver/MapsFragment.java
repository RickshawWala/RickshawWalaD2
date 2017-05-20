package com.aliv3.RickshawWalaDriver;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MapsFragment extends Fragment implements OnMapReadyCallback {

    Double destLat, destLong, originLat, originLong;
    Double fare;
    private GoogleMap mGoogleMap;

    public MapsFragment () {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps,container,false);

        originLat = getArguments().getDouble("originLat");
        originLong = getArguments().getDouble("originLong");
        destLat = getArguments().getDouble("destLat");
        destLong = getArguments().getDouble("destLong");
        fare = getArguments().getDouble("fare");

        MapFragment mapFragment = (MapFragment) getActivity().getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        final Button buttonFinishRide = (Button) rootView.findViewById(R.id.buttonFinishRide);
        buttonFinishRide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = Helper.getPreference("current_ride_id");
                try {
                    Helper.postRideUpdate(id, "payment pending", callback());
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
                                    bundle.putDouble("fare", fare);

                                    PaymentsFragment fragmentOperationPayments = new PaymentsFragment();
                                    fragmentOperationPayments.setArguments(bundle);
                                    android.support.v4.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                                    fragmentTransaction.replace(R.id.frame_main, fragmentOperationPayments);
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
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;

        LatLng destination = new LatLng(destLat, destLong); //latitude, longitude (of client) received from backend
        mGoogleMap.addMarker(new MarkerOptions()
                .position(destination)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET))
                .title("Destination"));

        LatLng origin = new LatLng(originLat, originLong);
        mGoogleMap.addMarker(new MarkerOptions()
                .position(origin)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title("Origin"));

        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(destination));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(destination , 15.0f));

    }

    private Bitmap getMarkerBitmapFromView(@DrawableRes int resId) {

        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.pickup_marker, null);
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.pickup_img);
        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
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
