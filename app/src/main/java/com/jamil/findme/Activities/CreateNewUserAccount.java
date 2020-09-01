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
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class CreateNewUserAccount extends AppCompatActivity {
    private static final String TAG = "TAG";
    private Spinner spinnerLocation;
    private CircularImageView ivStudent;
    public EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private String password;
    private Button btnRegisterStudent;
    private Uri mainImageUri = null;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progressDialog;
    private User currentUser;
    private PreferencesManager prefs;
    private StorageReference folderProfilePics;
    DatabaseReference tableUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_user_account);
        try {
            prefs=new PreferencesManager(this);
            currentUser=prefs.getCurrentUser();
            tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
            folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
            spinnerLocation = findViewById(R.id.spinnerLocation);
            etName = findViewById(R.id.etNameStudent);
            etEmail = findViewById(R.id.etEmailStudent);
            etPassword = findViewById(R.id.etPasswordStudent);
            etConfirmPassword = findViewById(R.id.etConfirmPassStudent);
            etPhone = findViewById(R.id.etPhoneStudent);
            ivStudent = findViewById(R.id.ivStudent);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
            setupSpinners();
            setupProgressDialog();

            btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location;
                    location = spinnerLocation.getSelectedItem().toString();
                    password = etPassword.getText().toString().trim();
                    final User visitor = new Visitor();
                    visitor.setName(etName.getText().toString().trim());
                    visitor.setEmail(etEmail.getText().toString().trim());
                    visitor.setLocation(location);
                    visitor.setPhone(etPhone.getText().toString().trim());
                    visitor.setType("User");
                    visitor.setPassword(etPassword.getText().toString());
                    // student.setToken(FirebaseInstanceId.getInstance().getToken());

                    if (validate(visitor)) {
                        progressDialog.show();
                        FirebaseAuth.getInstance().createUserWithEmailAndPassword
                                (visitor.getEmail(), password).
                                addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            visitor.setUid(FirebaseAuth.getInstance().getCurrentUser().getUid());

                                            firebaseDatabaseHelper.uploadFile(mainImageUri,
                                                    folderProfilePics.child(visitor.getUid() + ".jpg"),
                                                    new FirebaseDatabaseHelper.OnUploadFileCompleteListener() {
                                                        @Override
                                                        public void onUploadFileComplete(String url) {
                                                            visitor.setImage(url);
                                                            tableUser.child(visitor.getUid()).setValue(visitor).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        FirebaseAuth.getInstance().signInWithEmailAndPassword(currentUser.getEmail(),currentUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    progressDialog.dismiss();
                                                                                    finish();
                                                                                    Toast.makeText(CreateNewUserAccount.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();

                                                                                }
                                                                            }
                                                                        });

                                                                    } else {
                                                                        progressDialog.dismiss();
                                                                        Toast.makeText(CreateNewUserAccount.this, "Error :" + task.getException(), Toast.LENGTH_SHORT).show();

                                                                    }
                                                                }
                                                            });


                                                        }
                                                    });
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(CreateNewUserAccount.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }

            });
            ivStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(CreateNewUserAccount.this);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(CreateNewUserAccount.this);
        progressDialog.setTitle("Creating Your account");
        progressDialog.setMessage("Please wait while we setup your account information");
        progressDialog.setCancelable(false);
    }

    public boolean validate(User visitor) {
        if (mainImageUri == null) {
            Toast.makeText(CreateNewUserAccount.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (visitor.getName().isEmpty()) {
            etName.setError("Must Fill Field");
            etName.requestFocus();
            return false;
        }
        if (visitor.getEmail().isEmpty()) {
            etEmail.setError("Must Fill Field");
            etEmail.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(visitor.getEmail()).matches()) {
            etEmail.setError("Must enter valid Email");
            etEmail.requestFocus();
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
        if (spinnerLocation.getSelectedItem().equals("Location")) {
            spinnerLocation.requestFocus();
            ((TextView) spinnerLocation.getSelectedView()).setError("Select Your Location");
            return false;
        }
        if (visitor.getPhone().isEmpty()) {
            etPhone.setError("Must Fill Field");
            etPhone.requestFocus();
            return false;
        }
        if (visitor.getPhone().length() < 11) {
            etPhone.setError("Minimum length of password should be 6");
            etPhone.requestFocus();
            return false;
        }
        if (!password.equals(etConfirmPassword.getText().toString())) {
            etPassword.setError("Plz Match the passwords");
            etPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setupSpinners() {
        ArrayAdapter<String> deptArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
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
                Glide.with(this).load(mainImageUri).into(ivStudent);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e(TAG, "onActivityResult: " + error.toString());
            }
        }
    }

}