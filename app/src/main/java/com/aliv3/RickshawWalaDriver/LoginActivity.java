package com.aliv3.RickshawWalaDriver;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button Login;
    private EditText Email;
    private EditText Password;
    private TextView Register;

    private ProgressDialog ProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String accessToken = Helper.getPreference("access_token");
        String username = Helper.getPreference("username");
        String password = Helper.getPreference("password");

        if (accessToken != null) {
            completeLogin();
        } else { // to auto login after registration
            if(username != null && password != null) {
                try {
                    postGetToken(username, password);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        setContentView(R.layout.activity_login);

        Email = (EditText) findViewById(R.id.editsignemail);
        Password = (EditText) findViewById(R.id.editsignpassword);
        Login = (Button) findViewById(R.id.buttonlogin);
        Register = (TextView) findViewById(R.id.textregister);

        ProgressDialog = new ProgressDialog(this);

        Login.setOnClickListener(this);
        Register.setOnClickListener(this);
    }

    private void userLogin()
    {
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Please Enter your EmailID", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter your Password", Toast.LENGTH_SHORT).show();
            return;
        }

        ProgressDialog.setMessage("Signing In...");
        ProgressDialog.show();

        try {
            postGetToken(email, password);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {

        if(view == Login){
            userLogin();
        }

        if(view == Register)
        {
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }

    private void postGetToken(final String username, final String password) throws IOException, IllegalArgumentException {
        OkHttpClient client = Helper.getOkHttpClientInstance();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
                .build();
        Request request = new Request.Builder()
                .url("http://139.59.70.223/api/auth/token")
                .post(formBody)
                .build();

        client.newCall(request)
                .enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                                ProgressDialog.hide();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String jsonResponse = response.body().string();
//                            Log.d("RESPONSE", jsonResponse);
                            JSONObject jsonObject;
                            try {
                                jsonObject = new JSONObject(jsonResponse);
                                if (jsonObject.has("error")) {
                                    String error = jsonObject.getString("message");
                                    Log.d("POSTGetAccessToken", error);
                                } else if (jsonObject.has("access_token")) {
                                    String accessToken = jsonObject.getString("access_token");
                                    String refreshToken = jsonObject.getString("refresh_token");

                                    Helper.setPreference("username", username);
                                    Helper.setPreference("password", password);
                                    Helper.setPreference("access_token", accessToken);
                                    Helper.setPreference("refresh_token", refreshToken);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                                            completeLogin();
                                        }
                                    });
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(LoginActivity.this, "Internal Server Error", Toast.LENGTH_SHORT).show();
                                    ProgressDialog.hide();
                                }
                            });
                        }
                    }
                });
    }

    private void completeLogin() {
        Intent i = new Intent(LoginActivity.this, RideActivity.class);
        startActivity(i);
        finish();
    }

}
