package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

public class WorkShopProfile extends AppCompatActivity {

    private static final String TAG = "TAG";
    private CircularImageView ivWorkShopProfile;
    private TextView tvNameUserWorkShopProfile, tvCallUs, tvEditProfile, tvContactUs, tvLocationWorkShopProfile, tvNameWorkShopProfile, tvAddressWorkShopProfile, tvEmailWorkShopProfile, tvContactWorkShopProfile, tvDescWorkShopProfile;
    private WorkShopModel workShopModel;
    private User currentUser;
    private PreferencesManager preferencesManager;
    private ProgressDialog progressDialog;
    private DatabaseReference tableUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_shop_profile);
        try {
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            setupProgressDialog();
            preferencesManager = new PreferencesManager(this);
            currentUser = preferencesManager.getCurrentUser();
            initViews();
            workShopModel = new Gson().fromJson(getIntent().getStringExtra("WSP"), WorkShopModel.class);
            tvEditProfile = findViewById(R.id.tvEditProfile);
            tvCallUs = findViewById(R.id.tvCallUs);
            tvContactUs = findViewById(R.id.tvContactUs);
            if (workShopModel.getUid().equals(currentUser.getUid())) {
                tvEditProfile.setVisibility(View.VISIBLE);
            }
            SetProfile(workShopModel);
            if (currentUser.getUid().equals(workShopModel.getUid())) {
                tvContactUs.setVisibility(View.GONE);
                tvCallUs.setVisibility(View.GONE);
            } else {
                tvContactUs.setVisibility(View.VISIBLE);
                tvCallUs.setVisibility(View.VISIBLE);
            }
            tvEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(WorkShopProfile.this, EditVisitorInfoActivity.class);
                    intent.putExtra("UID", new Gson().toJson(workShopModel));
                    startActivity(intent);
                }
            });
            tvCallUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + workShopModel.getPhone()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(WorkShopProfile.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onClick: " + e.toString());
                    }
                }
            });
            tvContactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // progressDialog.show();
                    final ChatModel chatModel = new ChatModel();
                    final String chat_id = System.currentTimeMillis() + currentUser.getUid();
                    chatModel.setId(chat_id);
                    chatModel.setName(workShopModel.getName());
                    chatModel.setImage(workShopModel.getImage());
                    //     DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    tableUser.child(currentUser.getUid()).child("ChatList").child(workShopModel.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.e(TAG, "onDataChange: " + snapshot);
                            if (snapshot.getValue() != null) {
                                ChatModel chatMo = snapshot.getValue(ChatModel.class);
                                Log.e(TAG, "onDataChange: " + chatMo.getId());
                                Intent intent = new Intent(WorkShopProfile.this,
                                        ChatActivity.class);
                                intent.putExtra("CID", new Gson().toJson(chatMo));
                                startActivity(intent);
                                Toast.makeText(WorkShopProfile.this, "Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "onDataChange: Nullllll");
                                tableUser.child(currentUser.getUid()).child("ChatList").child(workShopModel.getUid()).setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            final ChatModel chatModel1 = new ChatModel();
                                            chatModel1.setName(currentUser.getName());
                                            chatModel1.setId(chat_id);
                                            chatModel1.setImage(currentUser.getImage());
                                            tableUser.child(workShopModel.getUid()).child("ChatList").child(currentUser.getUid()).setValue(chatModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(WorkShopProfile.this,
                                                            ChatActivity.class);
                                                    intent.putExtra("CID", new Gson().toJson(chatModel1));
                                                    startActivity(intent);
                                                    Toast.makeText(WorkShopProfile.this, "Chat Created Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(WorkShopProfile.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e(TAG, "onCancelled: " + error);
                        }
                    });


                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(WorkShopProfile.this);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
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
        tvContactUs = findViewById(R.id.tvContactUs);
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