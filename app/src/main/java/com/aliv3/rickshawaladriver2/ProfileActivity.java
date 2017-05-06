package com.aliv3.rickshawaladriver2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;

    private EditText Name;
    private EditText Phone;
    private EditText LicenseNo;
    private EditText VehicleNo;
    private Button SaveInfo;
    private Button Logout;
    private TextView Textprofile;

    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //print to log
        System.out.println("\n\n\n\t\tPROFILE ACTIVITY \n\n\n");

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() == null){
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        }

        databaseReference = FirebaseDatabase.getInstance().getReference();

        Name = (EditText) findViewById(R.id.editname);
        Phone = (EditText) findViewById(R.id.editphone);
        LicenseNo = (EditText) findViewById(R.id.editlicense);
        VehicleNo = (EditText) findViewById(R.id.editvehicle);
        SaveInfo = (Button) findViewById(R.id.buttonSaveinfo);


        FirebaseUser user = firebaseAuth.getCurrentUser();

        Textprofile = (TextView) findViewById(R.id.textViewprofile);

        Textprofile.setText("Welcome "+user.getEmail());


        Logout = (Button) findViewById(R.id.buttonlogout);

        Logout.setOnClickListener(this);
        SaveInfo.setOnClickListener(this);

    }

    private void saveDriverInformation(){
        String name = Name.getText().toString().trim();
        String phone = Phone.getText().toString().trim();
        String licenseNo = LicenseNo.getText().toString().trim();
        String vehicleNo = VehicleNo.getText().toString().trim();

        DriverInformation driverInformation = new DriverInformation(name,phone,licenseNo,vehicleNo);

        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(driverInformation);

        Toast.makeText(this,"Information Stored!",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onClick(View view) {

        if(view == Logout){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        if (view == SaveInfo){
            saveDriverInformation();
            //Go to map after saving details
            Intent i = new Intent(getBaseContext(),RideActivity.class);
            startActivity(i);
            finish();
        }

    }
}
