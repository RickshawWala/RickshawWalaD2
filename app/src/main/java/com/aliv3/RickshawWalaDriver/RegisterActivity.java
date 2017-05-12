package com.aliv3.RickshawWalaDriver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button Register;
    private EditText Email;
    private EditText Password;
    private TextView Signin;
    private EditText Name;
    private EditText MobileNumber;
    private EditText LicenseNumber;
    private EditText RegistrationNumber;

    private OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Register = (Button) findViewById(R.id.buttonregister);
        Email = (EditText) findViewById(R.id.editemail);
        Password = (EditText) findViewById(R.id.editpassword);
        Signin = (TextView) findViewById(R.id.textsignin);
        Name = (EditText) findViewById(R.id.editname);
        MobileNumber = (EditText) findViewById(R.id.editmobile);
        LicenseNumber = (EditText) findViewById(R.id.editlicense);
        RegistrationNumber = (EditText) findViewById(R.id.editvehicle);

        Register.setOnClickListener(this);
        Signin.setOnClickListener(this);
    }

    private void registerUser() {

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        String name = Name.getText().toString().trim();
        String mobileNumber = MobileNumber.getText().toString().trim();
        String licenseNumber = LicenseNumber.getText().toString().trim();
        String registrationNumber = RegistrationNumber.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter your EmailID", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "Please Enter your Name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mobileNumber)) {
            Toast.makeText(this, "Please Enter your Mobile Number", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(licenseNumber)) {
            Toast.makeText(this, "Please Enter your License Number", Toast.LENGTH_SHORT).show();
            return;
        }if (TextUtils.isEmpty(registrationNumber)) {
            Toast.makeText(this, "Please Enter your Vehicle Registration Number", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            postRegister(name, email, mobileNumber, password, licenseNumber, registrationNumber, "http://139.59.70.223/api/register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if (view == Register) {
            registerUser();
        }
        if (view == Signin)
        {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private void postRegister(String name, final String email, String mobileNumber, final String password, String licenseNumber, String registrationNumber, String url) throws IOException, IllegalArgumentException {
        RequestBody formBody = new FormBody.Builder()
                .add("name", name)
                .add("email", email)
                .add("mobile_number", mobileNumber)
                .add("password", password)
                .add("is_driver", "true")
                .add("licence_number", licenseNumber)
                .add("vehicle_registration_number", registrationNumber)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(RegisterActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonResponse = response.body().string();
//                        Log.d("RESPONSE", jsonResponse);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(jsonResponse);
                            String error = "", success = "";
                            if(jsonObject.has("error")) {
                                error = jsonObject.getString("error");
                            } else if(jsonObject.has("success")) {
                                success = jsonObject.getString("success");
                            }
                            uiHandle(error, success, email, password);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void uiHandle(final String error, final String success, final String email, final String password) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(error != "") {
                    Toast.makeText(RegisterActivity.this, error, Toast.LENGTH_SHORT).show();
                } else if (success != "") {
                    Toast.makeText(RegisterActivity.this, success, Toast.LENGTH_SHORT).show();

                    Helper.setPreference("username", email);
                    Helper.setPreference("password", password);

                    Intent i = new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }

}
