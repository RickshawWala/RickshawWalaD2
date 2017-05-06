package com.aliv3.rickshawaladriver2;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    private Button Login;
    private EditText Email;
    private EditText Password;
    private TextView Register;

    private FirebaseAuth firebaseAuth;
    private ProgressDialog ProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //print to log
        System.out.println("\n\n\n\t\tLOGIN ACTIVITY \n\n\n");

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser()!=null)
        {
            //profile activity here
            finish();
            startActivity((new Intent(getApplicationContext(),RideActivity.class)));
        }

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

        ProgressDialog.setMessage("Signing In...");
        ProgressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task)
                    {
                          ProgressDialog.dismiss();

                        if(task.isSuccessful())
                        {
                         //start the profile activity
                            finish();
                            startActivity((new Intent(getApplicationContext(),RideActivity.class)));

                        }
                    }
                });
    }

    @Override
    public void onClick(View view) {

        if(view == Login){
            userLogin();
            //Go to map after saving details
            Intent i = new Intent(LoginActivity.this, RideActivity.class);
            startActivity(i);
            finish();
        }

        if(view == Register)
        {
            startActivity(new Intent(this,RegisterActivity.class));
        }
    }
}
