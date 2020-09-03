package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.jamil.findme.Models.PostModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddSparePart extends AppCompatActivity implements FirebaseDatabaseHelper.OnPostCompleteListener {

    private static final String TAG = "TAG";
    private Uri newPostImgUri = null;
    Button btnAddSparePart;
    ImageView ivProduct;
    EditText etaspWorkShopName, etaspPModel, etaspPType, etaspPPrice, etaspDesc, etaspPName;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    Spinner spinnerLocation;
    private User currentUser;
    private PreferencesManager prefs;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_spare_part);
        try {
            initViews();
            setupProgressDialog();
            setupSpinners();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            initViews();
            ivProduct.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick: You Clik the Image");
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(40, 40)
                            .start(AddSparePart.this);
                }

            });
            btnAddSparePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm:ss");
                    String date = df.format(Calendar.getInstance().getTime());
                    PostModel postModel = new PostModel();
                    postModel.setProductName(etaspPName.getText().toString().trim());
                    postModel.setPrice(etaspPPrice.getText().toString().trim());
                    postModel.setDescription(etaspDesc.getText().toString().trim());
                    postModel.setType(etaspPType.getText().toString().trim());
                    postModel.setModel(etaspPModel.getText().toString().trim());
                    postModel.setWorkShop(etaspWorkShopName.getText().toString().trim());
                    postModel.setTime(date);
                    postModel.setLocation(spinnerLocation.getSelectedItem().toString());
                    postModel.setUser_id(currentUser.getUid());
                    if (validate(postModel)) {
                        progressDialog.show();
                        firebaseDatabaseHelper.sendPost(postModel, currentUser, newPostImgUri, AddSparePart.this);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(AddSparePart.this);
        progressDialog.setTitle("Creating Your Post");
        progressDialog.setMessage("Please wait while we setup your Data");
        progressDialog.setCancelable(false);
    }

    private void setupSpinners() {
        ArrayAdapter<String> deptArrayAdapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.Location));
        deptArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerLocation.setAdapter(deptArrayAdapter);

    }

    private boolean validate(PostModel postModel) {
        if (newPostImgUri == null) {
            Toast.makeText(AddSparePart.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (postModel.getProductName().isEmpty()) {
            etaspPName.setError("Must Fill Field");
            etaspPName.requestFocus();
            return false;
        }
        if (postModel.getDescription().isEmpty()) {
            etaspDesc.setError("Must Fill Field");
            etaspDesc.requestFocus();
            return false;
        }
        if (postModel.getPrice().isEmpty()) {
            etaspPPrice.setError("Must Fill Field");
            etaspPPrice.requestFocus();
            return false;
        }
        if (postModel.getType().isEmpty()) {
            etaspPType.setError("Must Fill Field");
            etaspPType.requestFocus();
            return false;
        }
        if (postModel.getModel().isEmpty()) {
            etaspPModel.setError("Must Fill Field");
            etaspPModel.requestFocus();
            return false;
        }
        if (postModel.getWorkShop().isEmpty()) {
            etaspWorkShopName.setError("Must Fill Field");
            etaspWorkShopName.requestFocus();
            return false;
        }
        if (spinnerLocation.getSelectedItem().equals("Location")) {
            spinnerLocation.requestFocus();
            ((TextView) spinnerLocation.getSelectedView()).setError("Select Your Location");
            return false;
        }
        return true;
    }

    private void initViews() {
        ivProduct = findViewById(R.id.ivProduct);
        spinnerLocation = findViewById(R.id.spinnerLocation);
        btnAddSparePart = findViewById(R.id.btnAddSparePart);
        etaspWorkShopName = findViewById(R.id.etaspWorkShopName);
        etaspPModel = findViewById(R.id.etaspPModel);
        etaspPType = findViewById(R.id.etaspPType);
        etaspPPrice = findViewById(R.id.etaspPPrice);
        etaspDesc = findViewById(R.id.etaspDesc);
        etaspPName = findViewById(R.id.etaspPName);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                newPostImgUri = result.getUri();
                Glide.with(this).load(newPostImgUri).into(ivProduct);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error =
                        result.getError();
                Log.e(TAG, "onActivityResult: " + error.toString());
            }
        }
    }

    @Override
    public void onPostCompleted(String isSuccessful) {
        progressDialog.dismiss();
        finish();
        Toast.makeText(this, "Message :" + isSuccessful, Toast.LENGTH_SHORT).show();
    }
}