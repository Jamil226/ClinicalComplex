package com.jamil.findme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;

public class LogInActivity extends AppCompatActivity
        implements FirebaseDatabaseHelper.OnLoginSignupAttemptCompleteListener {
    private EditText etemail, etpassword;
    private ProgressBar pbLogin;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private TextView tvsignUptecher, tvsignUpStudnt, tvLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_log_in);

        tvLogin = findViewById(R.id.btnLogIn);
        tvsignUpStudnt = findViewById(R.id.tvSignUpStudent);
        tvsignUptecher = findViewById(R.id.tvSignUpTeacher);
        pbLogin = findViewById(R.id.pbLogIn);
        etemail = findViewById(R.id.etEmail);
        etpassword = findViewById(R.id.etPassword);
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);


        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbLogin.setVisibility(View.VISIBLE);
                String email = etemail.getText().toString();
                String password = etpassword.getText().toString();

                if ((!TextUtils.isEmpty(email)) && (!TextUtils.isEmpty(password))) {
                    firebaseDatabaseHelper.attemptLogin(email, password, LogInActivity.this);
                } else {
                    pbLogin.setVisibility(View.INVISIBLE);
                    Toast.makeText(LogInActivity.this, "Please fill username and password fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        tvsignUptecher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, WorkCreateAccountActivity.class));
            }
        });

        tvsignUpStudnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LogInActivity.this, UserSignUpActivity.class));
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(LogInActivity.this, MainActivity.class));
            finish();
        }
    }

    @Override
    public void onLoginSignupSuccess(User user) {
        pbLogin.setVisibility(View.INVISIBLE);
        startActivity(new Intent(LogInActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onLoginSignupFailure(String failureMessage) {
        pbLogin.setVisibility(View.INVISIBLE);
        Toast.makeText(this, "Login Failed: " + failureMessage, Toast.LENGTH_SHORT).show();
    }
}
