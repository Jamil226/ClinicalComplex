package com.jamil.findme.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.jamil.findme.R;

public class ActivitySplash extends AppCompatActivity {
ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try{
            progressBar = findViewById(R.id.progressBar);

            new CountDownTimer(3000, 1000) {
                @Override
                public void onFinish() {
                    progressBar.setVisibility(View.GONE);
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onTick(long millisUntilFinished) {

                }
            }.start();

        }catch (Exception e){

        }
    }
}