package com.jamil.findme;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.mikhaellopez.circularimageview.CircularImageView;

public class UserProfileActivity extends AppCompatActivity {
    Visitor user;
    private static final String TAG = "TAG";
    private String uid;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    CircularImageView ivUserProfile;

    TextView tvNameUserProfile, tvPhoneUserProfile, tvLocationUserProfile, tvEmailUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        try {
            user = new Gson().fromJson(getIntent().getStringExtra("UID"), Visitor.class);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            ivUserProfile = findViewById(R.id.ivUserProfile);
            tvNameUserProfile = findViewById(R.id.tvNameUserProfile);
            tvPhoneUserProfile = findViewById(R.id.tvPhoneUserProfile);
            tvLocationUserProfile = findViewById(R.id.tvLocationUserProfile);
            tvEmailUserProfile = findViewById(R.id.tvEmailUserProfile);

            Glide.with(this).load(user.getImage()).into(ivUserProfile);
            tvNameUserProfile.setText(user.getName());
            tvEmailUserProfile.setText(user.getEmail());
            tvPhoneUserProfile.setText(user.getPhone());
            tvLocationUserProfile.setText(user.getLocation());
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}