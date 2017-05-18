package com.aliv3.RickshawWalaDriver;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

public class PaymentsFragment extends Fragment {

    public PaymentsFragment () {
        //Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_payments,container,false);

        final ImageButton buttonCash = (ImageButton) rootView.findViewById(R.id.buttonCash);
        final ImageButton buttonPaytm = (ImageButton) rootView.findViewById(R.id.buttonPaytm);
        final Button buttonGoToMap = (Button) rootView.findViewById(R.id.buttonGoHome);

        buttonCash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Pay Cash", Toast.LENGTH_SHORT).show();
            }
        });

        buttonPaytm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Go to Paytm", Toast.LENGTH_SHORT).show();

            }
        });

        buttonGoToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getFragmentManager().beginTransaction().remove(PaymentsFragment.this).commitAllowingStateLoss();
                Intent intent = new Intent(getActivity(), RideActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
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
