package com.jamil.findme.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.jamil.findme.Models.GeneralRepairModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddGeneralVehicleMaintenanceItem extends AppCompatActivity implements FirebaseDatabaseHelper.onAddGeneralVehicleMaintenanceItemCompleteListener{
    private static final String TAG = "TAG";
    private Uri newPostImgUri = null;
    Button btnAddSparePart;
    ImageView ivProduct;
    EditText etDetails,etTitle;
    LinearLayout llAddPhoto;
    ProgressDialog progressDialog;
    PreferencesManager pref;
    User currentUser;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_general_vehicle_maintenance_item);
        try{
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            pref=new PreferencesManager(this);
            currentUser=pref.getCurrentUser();
            setupProgressDialog();
            btnAddSparePart=findViewById(R.id.btnAdd);
            etTitle=findViewById(R.id.etTitle);
            etDetails=findViewById(R.id.etDetails);
            ivProduct=findViewById(R.id.ivProduct);
            llAddPhoto=findViewById(R.id.llAddPhoto);
            llAddPhoto.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(40, 40)
                            .start(AddGeneralVehicleMaintenanceItem.this);
                }

            });
            btnAddSparePart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                    String date = df.format(Calendar.getInstance().getTime());
                    GeneralRepairModel postModel=new GeneralRepairModel();
                    postModel.setId(System.currentTimeMillis()+currentUser.getUid());
                    postModel.setTitle(etTitle.getText().toString());
                    postModel.setDescription(etDetails.getText().toString());
                    postModel.setUsername(currentUser.getName());
                    postModel.setDate(date);
                    if (newPostImgUri == null) {
                        Toast.makeText(AddGeneralVehicleMaintenanceItem.this, "Please Choose Image First", Toast.LENGTH_SHORT).show();
                        return ;
                    }
                    if (postModel.getTitle().isEmpty()) {
                        etTitle.setError("Must Fill Field");
                        etTitle.requestFocus();
                        return ;
                    }
                    if (postModel.getDescription().isEmpty()) {
                        etDetails.setError("Must Fill Field");
                        etDetails.requestFocus();
                        return ;
                    }else{
                        progressDialog.show();
                        firebaseDatabaseHelper.addGeneralVehicleMaintenanceItem(postModel,newPostImgUri,AddGeneralVehicleMaintenanceItem.this);
                    }
                }
            });
        }catch (Exception e){
            Log.e(TAG, "onCreate: "+e.toString() );
        }

    }
    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(AddGeneralVehicleMaintenanceItem.this);
        progressDialog.setTitle("Sending");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
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
    public void onVehicleMaintenanceItemCompleted(String models) {
        progressDialog.dismiss();
        finish();
        Toast.makeText(this, "Added"+models, Toast.LENGTH_SHORT).show();

    }
}