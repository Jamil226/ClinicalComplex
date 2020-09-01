package com.jamil.findme.Activities;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.mikhaellopez.circularimageview.CircularImageView;

public class WorkShopProfile extends AppCompatActivity {

    private static final String TAG = "TAG";
    CircularImageView ivWorkShopProfile;
    TextView tvNameUserWorkShopProfile, tvLocationWorkShopProfile, tvNameWorkShopProfile, tvAddressWorkShopProfile, tvEmailWorkShopProfile, tvContactWorkShopProfile, tvDescWorkShopProfile;
    WorkShopModel workShopModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_shop_profile);
        try {
            initViews();
            workShopModel = new Gson().fromJson(getIntent().getStringExtra("WSP"), WorkShopModel.class);
            SetProfile(workShopModel);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void SetProfile(WorkShopModel workShopModel) {
        Glide.with(this).load(workShopModel.getImage()).into(ivWorkShopProfile);
        tvLocationWorkShopProfile.setText(workShopModel.getLocation());
        tvNameWorkShopProfile.setText(workShopModel.getWorkShopName());
        tvNameUserWorkShopProfile.setText(workShopModel.getName());
        tvAddressWorkShopProfile.setText(workShopModel.getAddress());
        tvEmailWorkShopProfile.setText(workShopModel.getEmail());
        tvContactWorkShopProfile.setText(workShopModel.getPhone());
        tvDescWorkShopProfile.setText(workShopModel.getDescription());

    }

    private void initViews() {
        ivWorkShopProfile = findViewById(R.id.ivWorkShopProfile);
        tvLocationWorkShopProfile = findViewById(R.id.tvLocationWorkShopProfile);
        tvNameUserWorkShopProfile = findViewById(R.id.tvNameUserWorkShopProfile);
        tvNameWorkShopProfile = findViewById(R.id.tvNameWorkShopProfile);
        tvAddressWorkShopProfile = findViewById(R.id.tvAddressWorkShopProfile);
        tvEmailWorkShopProfile = findViewById(R.id.tvEmailWorkShopProfile);
        tvContactWorkShopProfile = findViewById(R.id.tvContactWorkShopProfile);
        tvDescWorkShopProfile = findViewById(R.id.tvDescWorkShopProfile);
    }
}