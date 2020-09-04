package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class EditWorkShopInfo extends AppCompatActivity {
    private static final String TAG = "TeacherSignUpActivity";
    private Spinner spinnerLocation;
    private CircularImageView ivProfileTeacher;
    public EditText etName, etEmail, etPassword,
            etConfirmPassword, etPhone,
            etWorkShopName, etDescrip, etAddress;
    private String password;
    private Button btnRegisterStudent;
    private Uri mainImageUri = null;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progressDialog;
    private StorageReference folderProfilePics;
    private WorkShopModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work_shop_info);
        try {
            folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
            intitViews();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            userModel = new Gson().fromJson(getIntent().getStringExtra("WS"), WorkShopModel.class);
            setUpData(userModel);
            btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location;
                    location = spinnerLocation.getSelectedItem().toString();
                    password = etPassword.getText().toString().trim();
                    final WorkShopModel workShopModel = new WorkShopModel();
                    workShopModel.setUid(userModel.getUid());
                    workShopModel.setLocation(location);
                    workShopModel.setWorkShopName(etWorkShopName.getText().toString().trim());
                    workShopModel.setDescription(etDescrip.getText().toString().trim());
                    workShopModel.setAddress(etAddress.getText().toString().trim());
                    workShopModel.setName(etName.getText().toString().trim());
                    workShopModel.setEmail(userModel.getEmail());
                    workShopModel.setPhone(etPhone.getText().toString().trim());
                    workShopModel.setType("WorkShop");
                    workShopModel.setPassword(etPassword.getText().toString());
                    //  supervisor.setToken(FirebaseInstanceId.getInstance().getToken());

                    if (validate(workShopModel)) {
                        progressDialog.show();
                        if (mainImageUri == null) {

                            workShopModel.setImage(userModel.getImage());
                            updateData(workShopModel);
                        } else {
                            folderProfilePics.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "onUploadFileComplete: Now uploadiong file");
                                        folderProfilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                workShopModel.setImage(uri.toString());
                                                updateData(workShopModel);
                                            }
                                        });
                                    }
                                }
                            });

                        }

                        //firebaseDatabaseHelper.attemptSignUp(workShopModel, password, mainImageUri, EditWorkShopInfo.this);
                    }
                }
            });

            ivProfileTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(EditWorkShopInfo.this);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void updateData(final WorkShopModel workShopModel) {
        final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
        tableUser.child(workShopModel.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        Map<String, Object> postValues = new HashMap<String, Object>();
                                                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                                            postValues.put(snapshot.getKey(), snapshot.getValue());
                                                        }
                                                        postValues.put("image", workShopModel.getImage());
                                                        postValues.put("email", workShopModel.getEmail());
                                                        postValues.put("name", workShopModel.getName());
                                                        postValues.put("phone", workShopModel.getPhone());
                                                        postValues.put("workShopName", workShopModel.getWorkShopName());
                                                        postValues.put("password", workShopModel.getPassword());
                                                        postValues.put("location", workShopModel.getLocation());
                                                        postValues.put("address", workShopModel.getAddress());
                                                        tableUser.child(workShopModel.getUid()).updateChildren(postValues);
                                                        progressDialog.dismiss();
                                                        finish();
                                                        Toast.makeText(EditWorkShopInfo.this, "Updated", Toast.LENGTH_SHORT).show();
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {
                                                        Log.e(TAG, "onCancelled: " + databaseError);
                                                        progressDialog.dismiss();
                                                        Toast.makeText(EditWorkShopInfo.this, "Error" + databaseError, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                );

    }

    private void setUpData(WorkShopModel userModel) {
        Glide.with(this).load(userModel.getImage()).into(ivProfileTeacher);
        etWorkShopName.setText(userModel.getWorkShopName());
        etDescrip.setText(userModel.getDescription());
        etAddress.setText(userModel.getAddress());
        etName.setText(userModel.getName());
        etPassword.setText(userModel.getPassword());
        etConfirmPassword.setText(userModel.getPassword());
        etPhone.setText(userModel.getPhone());

    }

    private void intitViews() {

        etWorkShopName = findViewById(R.id.etsvWorkShopName);
        etDescrip = findViewById(R.id.etsvDesc);
        etAddress = findViewById(R.id.etsvAddress);
        spinnerLocation = findViewById(R.id.spinnersvLocation);
        etName = findViewById(R.id.etTeacherName);
        etPassword = findViewById(R.id.etsvPassword);
        etConfirmPassword = findViewById(R.id.etsvConfirmPassword);
        etPhone = findViewById(R.id.etsvPhone);
        ivProfileTeacher = findViewById(R.id.ivProfileTeacher);
        setupSpinners();
        setupProgressDialog();
        btnRegisterStudent = findViewById(R.id.btnsvRegister);
    }

    public boolean validate(WorkShopModel workShopModel) {

        if (workShopModel.getName().isEmpty()) {
            etName.setError("Must Fill Field");
            etName.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(workShopModel.getEmail()).matches()) {
            etEmail.setError("Must enter valid Email");
            etEmail.requestFocus();
            return false;
        }
        if (workShopModel.getPhone().isEmpty()) {
            etPhone.setError("Must Fill Field");
            etPhone.requestFocus();
            return false;
        }
        if (workShopModel.getPhone().length() < 7) {
            etPhone.setError("Minimum length of contact no should be 7");
            etPhone.requestFocus();
            return false;
        }
        if (spinnerLocation.getSelectedItem().equals("Location")) {
            spinnerLocation.requestFocus();
            ((TextView) spinnerLocation.getSelectedView()).setError("Select Your Location");
            return false;
        }
        if (workShopModel.getWorkShopName().isEmpty()) {
            etWorkShopName.setError("Must Fill Field");
            etWorkShopName.requestFocus();
            return false;
        }
        if (workShopModel.getDescription().isEmpty()) {
            etDescrip.setError("Must Fill Field");
            etDescrip.requestFocus();
            return false;
        }
        if (workShopModel.getAddress().isEmpty()) {
            etAddress.setError("Must Fill Field");
            etAddress.requestFocus();
            return false;
        }
        if (password.isEmpty()) {
            etPassword.setError("Must Fill Field");
            etPassword.requestFocus();
            return false;
        }
        if (etConfirmPassword.getText().toString().isEmpty()) {
            etConfirmPassword.setError("Must Fill Field");
            etConfirmPassword.requestFocus();
            return false;
        }


        if (!password.equals(etConfirmPassword.getText().toString())) {
            etPassword.setError("Plz Match the passwords");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(EditWorkShopInfo.this);
        progressDialog.setTitle("Creating Your account");
        progressDialog.setMessage("Please wait while we setup your account information");
        progressDialog.setCancelable(false);
    }

    private void setupSpinners() {
        ArrayAdapter<String> deptArrayAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item,
                        getResources().getStringArray(R.array.Location));
        deptArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(deptArrayAdapter);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mainImageUri = result.getUri();
                Glide.with(this).load(mainImageUri).into(ivProfileTeacher);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: " + error.toString());
            }
        }
    }

}