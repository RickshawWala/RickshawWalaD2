package com.aliv3.rickshawaladriver2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Register;
    private EditText Email;
    private EditText Password;
    private TextView Signin;

    //Database
    private DatabaseReference databaseReference;

    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //print to log
        System.out.println("\n\n\n\t\tREGISTER ACTIVITY \n\n\n");

        Register = (Button) findViewById(R.id.buttonregister);
        Email = (EditText) findViewById(R.id.editemail);
        Password = (EditText) findViewById(R.id.editpassword);
        Signin = (TextView) findViewById(R.id.textsignin);

        ProgressDialog = new ProgressDialog(this);

        Register.setOnClickListener(this);
        Signin.setOnClickListener(this);

    }

    private void registerUser() {

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            //email is empty
            Toast.makeText(this, "Please Enter your EmailID", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }
        if (TextUtils.isEmpty(password)) {
            //password is empty

            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
            //stops the function from executing further
            return;
        }
        //if validations are ok
        // we will show progress dialog

        ProgressDialog.setMessage("Registering...");
        ProgressDialog.show();
    }

    @Override
    public void onClick(View view) {

        if (view == Register) {
            registerUser();
        }
        if (view == Signin)
        {
            //open login activity here
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }
}
