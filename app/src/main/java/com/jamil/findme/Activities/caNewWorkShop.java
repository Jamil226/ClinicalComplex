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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class caNewWorkShop extends AppCompatActivity {
    private static final String TAG = "TAG";
    private Spinner spinnerLocation;
    private CircularImageView ivProfileTeacher;
    public EditText etName, etEmail, etPassword,
            etConfirmPassword, etPhone,
            etWorkShopName, etDescrip, etAddress;
    private String password;
    private User currentUser;
    private PreferencesManager prefs;
    private Button btnRegisterStudent;
    private Uri mainImageUri = null;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    private StorageReference folderProfilePics;
    private DatabaseReference tableUser;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ca_new_work_shop);

        try {

            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
            intitViews();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location;
                    location = spinnerLocation.getSelectedItem().toString();
                    password = etPassword.getText().toString().trim();
                    final WorkShopModel workShopModel = new WorkShopModel();
                    workShopModel.setLocation(location);
                    workShopModel.setWorkShopName(etWorkShopName.getText().toString().trim());
                    workShopModel.setDescription(etDescrip.getText().toString().trim());
                    workShopModel.setAddress(etAddress.getText().toString().trim());
                    workShopModel.setName(etName.getText().toString().trim());
                    workShopModel.setEmail(etEmail.getText().toString().trim());
                    workShopModel.setPhone(etPhone.getText().toString().trim());
                    workShopModel.setType("WorkShop");
                    workShopModel.setPassword(etPassword.getText().toString());
                    //  supervisor.setToken(FirebaseInstanceId.getInstance().getToken());

                    if (validate(workShopModel)) {
                        progressDialog.show();
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword
                                (workShopModel.getEmail(), password).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            workShopModel.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            firebaseDatabaseHelper.uploadFile(mainImageUri,
                                                    folderProfilePics.child(workShopModel.getUid() + ".jpg"),
                                                    new FirebaseDatabaseHelper.OnUploadFileCompleteListener() {
                                                        @Override
                                                        public void onUploadFileComplete(String url) {
                                                            workShopModel.setImage(url);
                                                            tableUser.child(workShopModel.getUid()).
                                                                    setValue(workShopModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.getEmail(), currentUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    progressDialog.dismiss();
                                                                                    finish();
                                                                                    Toast.makeText(caNewWorkShop.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(caNewWorkShop.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });


                                                        }
                                                    });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(caNewWorkShop.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
            });

            ivProfileTeacher.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(caNewWorkShop.this);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void intitViews() {

        etWorkShopName = findViewById(R.id.etsvWorkShopName);
        etDescrip = findViewById(R.id.etsvDesc);
        etAddress = findViewById(R.id.etsvAddress);
        spinnerLocation = findViewById(R.id.spinnersvLocation);
        etName = findViewById(R.id.etTeacherName);
        etEmail = findViewById(R.id.etTeacherEmail);
        etPassword = findViewById(R.id.etsvPassword);
        etConfirmPassword = findViewById(R.id.etsvConfirmPassword);
        etPhone = findViewById(R.id.etsvPhone);
        ivProfileTeacher = findViewById(R.id.ivProfileTeacher);
        setupSpinners();
        setupProgressDialog();
        btnRegisterStudent = findViewById(R.id.btnsvRegister);
    }

    public boolean validate(WorkShopModel workShopModel) {
        if (mainImageUri == null) {
            Toast.makeText(caNewWorkShop.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (workShopModel.getName().isEmpty()) {
            etName.setError("Must Fill Field");
            etName.requestFocus();
            return false;
        }
        if (workShopModel.getEmail().isEmpty()) {
            etEmail.setError("Must Fill Field");
            etEmail.requestFocus();
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
        progressDialog = new ProgressDialog(caNewWorkShop.this);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}