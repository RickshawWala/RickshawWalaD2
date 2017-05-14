package com.aliv3.RickshawWalaDriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private TextView Name;
    private TextView Email;
    private TextView MobileNumber;
    private TextView LicenseNumber;
    private TextView RegistrationNumber;
    private Button Logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Name = (TextView) findViewById(R.id.name);
        Email = (TextView) findViewById(R.id.email);
        MobileNumber = (TextView) findViewById(R.id.mobileNumber);
        LicenseNumber = (TextView) findViewById(R.id.licenseNumber);
        RegistrationNumber = (TextView) findViewById(R.id.registrationNumber);

        Logout = (Button) findViewById(R.id.buttonlogout);
        Logout.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if(view == Logout){

            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
