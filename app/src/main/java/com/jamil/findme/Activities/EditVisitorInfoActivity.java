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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class EditVisitorInfoActivity extends AppCompatActivity implements FirebaseDatabaseHelper.onVisitorDataUpdateListener {
    private static final String TAG = "TAG";
    private Spinner spinnerLocation;
    private CircularImageView ivStudent;
    public EditText etName, etEmail, etPassword, etConfirmPassword, etPhone;
    private String password;
    private Button btnRegisterStudent;
    private Uri mainImageUri = null;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progressDialog;
    private Visitor user;
    private StorageReference folderProfilePics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitor_info);
        try {
            folderProfilePics = FirebaseStorage.getInstance().getReference().child("profile_image");
            etName = findViewById(R.id.etNameStudentEditVisitor);
            spinnerLocation = findViewById(R.id.spinnerLocationEditVisitor);
            etEmail = findViewById(R.id.etEmailStudentEditVisitor);
            etPassword = findViewById(R.id.etPasswordStudentEditVisitor);
            etConfirmPassword = findViewById(R.id.etConfirmPassStudentEditVisitor);
            etPhone = findViewById(R.id.etPhoneStudentEditVisitor);
            ivStudent = findViewById(R.id.ivEditVisitor);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            btnRegisterStudent = findViewById(R.id.btnRegisterStudentEditVisitor);
            setupSpinners();
            setupProgressDialog();
            user = new Gson().fromJson(getIntent().getStringExtra("UID"), Visitor.class);
            setDataOnFields(user);
            btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String location;
                    location = spinnerLocation.getSelectedItem().toString();
                    password = etPassword.getText().toString().trim();
                    final Visitor visitor = new Visitor();
                    visitor.setName(etName.getText().toString().trim());
                    visitor.setEmail(etEmail.getText().toString().trim());
                    visitor.setLocation(location);
                    visitor.setPhone(etPhone.getText().toString().trim());
                    visitor.setType("User");
                    visitor.setPassword(etPassword.getText().toString());
                    // student.setToken(FirebaseInstanceId.getInstance().getToken());

                    if (validate(visitor)) {
                        progressDialog.show();
                        if (mainImageUri == null) {
                            progressDialog.show();
                            visitor.setImage(user.getImage());
                            firebaseDatabaseHelper.UpdateData(visitor,
                                    user, mainImageUri, EditVisitorInfoActivity.this);
                        } else {
                            progressDialog.show();
                            folderProfilePics.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                    if (task.isSuccessful()) {
                                        Log.e(TAG, "onUploadFileComplete: Now uploadiong file");
                                        folderProfilePics.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                visitor.setImage(uri.toString());
                                                firebaseDatabaseHelper.UpdateData(visitor,
                                                        user, mainImageUri, EditVisitorInfoActivity.this);
                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }
                }

            });

            ivStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(EditVisitorInfoActivity.this);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }


    private void setDataOnFields(Visitor user) {
        Glide.with(this).load(user.getImage()).into(ivStudent);
        etName.setText(user.getName());
        etEmail.setText(user.getEmail());
        etPassword.setText(user.getPassword());
        etConfirmPassword.setText(user.getPassword());
        etPhone.setText(user.getPhone());
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(EditVisitorInfoActivity.this);
        progressDialog.setTitle("Updating Your account");
        progressDialog.setMessage("Please wait while we setup your account information");
        progressDialog.setCancelable(false);
    }

    public boolean validate(Visitor visitor) {
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
        if (visitor.getPhone().length() < 7) {
            etPhone.setError("Minimum length of contact no should be 7");
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

    @Override
    public void onVisitorDataUpdateCompleted(String success) {
        if (success.equals("success")) {
            progressDialog.dismiss();
            Toast.makeText(this, "Data Updated Successfully", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.dismiss();
            Toast.makeText(this, "Error :" + success, Toast.LENGTH_SHORT).show();
        }
    }
}
