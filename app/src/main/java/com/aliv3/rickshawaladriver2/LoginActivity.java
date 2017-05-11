package com.aliv3.rickshawaladriver2;

import android.app.ProgressDialog;
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
    private final OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //print to log
        System.out.println("\n\n\n\t\tLOGIN ACTIVITY \n\n\n");

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
            postGetToken(email, password, "http://139.59.70.223/api/auth/token");
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

    private void postGetToken(String username, String password, String url) throws IOException, IllegalArgumentException {
        OkHttpClient client = new OkHttpClient();

        RequestBody formBody = new FormBody.Builder()
                .add("username", username)
                .add("password", password)
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
                                Toast.makeText(LoginActivity.this, "Connection Error", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String jsonResponse = response.body().string();
//                        Log.d("RESPONSE", res);
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(jsonResponse);
                            String error = "", accessToken = "", refreshToken = "";
                            if(jsonObject.has("error")) {
                                error = jsonObject.getString("message");
                            } else if(jsonObject.has("access_token")) {
                                accessToken = jsonObject.getString("access_token");
                                refreshToken = jsonObject.getString("refresh_token");
                            }
                            uiHandle(error, accessToken, refreshToken);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private void uiHandle(final String error, final String accessToken, final String refreshToken) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(error != "") {
                    Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                } else if (accessToken != "") {
                    Toast.makeText(LoginActivity.this, accessToken, Toast.LENGTH_SHORT).show();
                    
                    //Go to map after saving details
                    Intent i = new Intent(LoginActivity.this, RideActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });
    }
}
