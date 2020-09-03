package com.jamil.findme.Fragments;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jamil.findme.Activities.ChatActivity;
import com.jamil.findme.Activities.EditVisitorInfoActivity;
import com.jamil.findme.Activities.WorkShopProfile;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class MyProfileFragment extends Fragment {

    private static final String TAG="TAG";
    private CircularImageView ivWorkShopProfile;
    private TextView tvNameUserWorkShopProfile, tvCallUs, tvEditProfile, tvContactUs, tvLocationWorkShopProfile, tvNameWorkShopProfile, tvAddressWorkShopProfile, tvEmailWorkShopProfile, tvContactWorkShopProfile, tvDescWorkShopProfile;
    private WorkShopModel workShopModel;
    private User currentUser;
    private PreferencesManager preferencesManager;
    private ProgressDialog progressDialog;
    private DatabaseReference tableUser;
    public MyProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_my_profile, container, false);
        try {
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            preferencesManager = new PreferencesManager(getContext());
            currentUser = preferencesManager.getCurrentUser();
            initViews(view);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }

        return view;
    }
    private void initViews(View view) {
        tvContactUs = view.findViewById(R.id.tvContactUs);
        ivWorkShopProfile = view.findViewById(R.id.ivWorkShopProfile);
        tvLocationWorkShopProfile = view.findViewById(R.id.tvLocationWorkShopProfile);
        tvNameUserWorkShopProfile = view.findViewById(R.id.tvNameUserWorkShopProfile);
        tvNameWorkShopProfile = view.findViewById(R.id.tvNameWorkShopProfile);
        tvAddressWorkShopProfile =view.findViewById(R.id.tvAddressWorkShopProfile);
        tvEmailWorkShopProfile = view.findViewById(R.id.tvEmailWorkShopProfile);
        tvContactWorkShopProfile = view.findViewById(R.id.tvContactWorkShopProfile);
        tvDescWorkShopProfile = view.findViewById(R.id.tvDescWorkShopProfile);
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

    @Override
    public void onStart() {
        super.onStart();
        tableUser.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<WorkShopModel> arrayList=new ArrayList<>();
                arrayList.clear();
                workShopModel=snapshot.getValue(WorkShopModel.class);
                SetProfile(workShopModel);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Error :"+error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}