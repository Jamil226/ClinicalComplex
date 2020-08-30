package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


public class WorkCreateAccountActivity extends AppCompatActivity implements FirebaseDatabaseHelper.OnLoginSignupAttemptCompleteListener {
    private static final String TAG = "TeacherSignUpActivity";
    private Spinner spinnerLocation;
    private BroadcastReceiver broadcastReceiver;
    private CircularImageView ivProfileTeacher;
    public EditText etName, etEmail, etPassword,
            etConfirmPassword, etPhone,
            etWorkShopName, etDescrip, etAddress;
    private String password;
    private Button btnRegisterStudent;
    private Uri mainImageUri = null;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workshop_createaccount);

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
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);

        btnRegisterStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String location;
                location = spinnerLocation.getSelectedItem().toString();
                password = etPassword.getText().toString().trim();
                WorkShopModel workShopModel = new WorkShopModel();
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
                    firebaseDatabaseHelper.attemptSignUp(workShopModel, password, mainImageUri, WorkCreateAccountActivity.this);
                }
            }
        });

    /*    broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context, "its if phase", Toast.LENGTH_SHORT).show();
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(MyFirebaseInstanceIDService.TOKEN_BROADCAST));
*/
        ivProfileTeacher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(WorkCreateAccountActivity.this);
            }
        });

    }


    @Override
    public void onLoginSignupSuccess(User user) {
        progressDialog.dismiss();
        startActivity(new Intent(WorkCreateAccountActivity.this, MainActivity.class));
        finish();
        Toast.makeText(WorkCreateAccountActivity.this, "Account Created Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLoginSignupFailure(String failureMessage) {
        progressDialog.dismiss();
        Toast.makeText(this, "Failed to create account: " + failureMessage, Toast.LENGTH_SHORT).show();
    }

    public boolean validate(WorkShopModel workShopModel) {
        if (mainImageUri == null) {
            Toast.makeText(WorkCreateAccountActivity.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
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
        if (workShopModel.getPhone().length() < 11) {
            etPhone.setError("Minimum length of password should be 6");
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
        progressDialog = new ProgressDialog(WorkCreateAccountActivity.this);
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
