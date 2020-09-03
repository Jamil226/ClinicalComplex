package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

public class UserProfileActivity extends AppCompatActivity {
    Visitor user;
    private static final String TAG = "TAG";
    private String uid;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    CircularImageView ivUserProfile;
    private DatabaseReference tableUser;
    private ProgressDialog progressDialog;
    Button tvContactUs;
    PreferencesManager preferencesManager;
    User currentUser;
    TextView tvNameUserProfile,tvEditProfile,tvCallUs
, tvPhoneUserProfile, tvLocationUserProfile, tvEmailUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        try {
            setupProgressDialog();
            preferencesManager = new PreferencesManager(this);
            currentUser = preferencesManager.getCurrentUser();
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            user = new Gson().fromJson(getIntent().getStringExtra("UID"), Visitor.class);

            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            tvEditProfile = findViewById(R.id.tvEditProfile);
            tvCallUs = findViewById(R.id.tvCallUsUser);
            if(user.getUid().equals(currentUser.getUid())){
                tvEditProfile.setVisibility(View.VISIBLE);
            }
            tvEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(UserProfileActivity.this, EditVisitorInfoActivity.class);
                    intent.putExtra("UID", new Gson().toJson(user));
                    startActivity(intent);
                }
            });
            tvCallUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:" + user.getPhone()));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        Toast.makeText(UserProfileActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "onClick: " + e.toString());
                    }
                }
            });

            tvContactUs = findViewById(R.id.tvContactUs);
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
            if(currentUser.getUid().equals(user.getUid())){
                tvContactUs.setVisibility(View.GONE);
                tvCallUs.setVisibility(View.GONE);
            }
            tvContactUs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // progressDialog.show();
                    final ChatModel chatModel = new ChatModel();
                    final String chat_id = System.currentTimeMillis() + currentUser.getUid();
                    chatModel.setId(chat_id);
                    chatModel.setName(user.getName());
                    chatModel.setImage(user.getImage());
                    //     DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
                    tableUser.child(currentUser.getUid()).child("ChatList").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            Log.e(TAG, "onDataChange: " + snapshot);
                            if (snapshot.getValue() != null) {
                                // run some code
                                ChatModel chatMo = snapshot.getValue(ChatModel.class);
                                Log.e(TAG, "onDataChange: " + chatMo.getId());
                                Intent intent = new Intent(UserProfileActivity.this,
                                        ChatActivity.class);
                                intent.putExtra("CID", new Gson().toJson(chatMo));
                                startActivity(intent);
                                Toast.makeText(UserProfileActivity.this, "Exist", Toast.LENGTH_SHORT).show();
                            } else {
                                Log.e(TAG, "onDataChange: Nullllll");
                                tableUser.child(currentUser.getUid()).child("ChatList").child(user.getUid()).setValue(chatModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            final ChatModel chatModel1 = new ChatModel();
                                            chatModel1.setName(currentUser.getName());
                                            chatModel1.setId(chat_id);
                                            chatModel1.setImage(currentUser.getImage());
                                            tableUser.child(user.getUid()).child("ChatList").child(currentUser.getUid()).setValue(chatModel1).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    progressDialog.dismiss();
                                                    Intent intent = new Intent(UserProfileActivity.this,
                                                            ChatActivity.class);
                                                    intent.putExtra("CID", new Gson().toJson(chatModel1));
                                                    startActivity(intent);
                                                    Toast.makeText(UserProfileActivity.this, "Chat Created Successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        } else {
                                            Toast.makeText(UserProfileActivity.this, "Error : " + task.getException(), Toast.LENGTH_SHORT).show();
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
        progressDialog = new ProgressDialog(UserProfileActivity.this);
        progressDialog.setTitle("Loading..");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}