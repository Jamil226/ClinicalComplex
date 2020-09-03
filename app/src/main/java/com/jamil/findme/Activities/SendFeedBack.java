package com.jamil.findme.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.jamil.findme.Models.FeedBackModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

public class SendFeedBack extends AppCompatActivity implements FirebaseDatabaseHelper.onSendFeedBackDataCompleteListener {
    private static final String TAG = "TAG";
    User currentUser;
    PreferencesManager pref;
    Button btnSendFeedBack;
    TextView etSubject, etMessage;
    FirebaseDatabaseHelper firebaseDatabaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_feed_back);
        try {
            pref = new PreferencesManager(this);
            currentUser = pref.getCurrentUser();
            firebaseDatabaseHelper=new FirebaseDatabaseHelper(this);
            btnSendFeedBack = findViewById(R.id.btnSendFeedBack);
            etSubject = findViewById(R.id.etSubject);
            etMessage = findViewById(R.id.etMessage);
            btnSendFeedBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String message,subject;
                    message=etMessage.getText().toString().trim();
                    subject=etSubject.getText().toString().trim();
                    if(TextUtils.isEmpty(subject))
                    {
                        etSubject.setError("Must Fill Field");
                        etSubject.requestFocus();
                        return;
                    }
                    if(TextUtils.isEmpty(subject))
                    {
                        etSubject.setError("Must Fill Field");
                        etSubject.requestFocus();
                        return;
                    }else{
                        FeedBackModel feedBackModel=new FeedBackModel();
                        feedBackModel.setSubject(subject);
                        feedBackModel.setMessage(message);
                        feedBackModel.setEmail(currentUser.getEmail());
                        feedBackModel.setUid(currentUser.getUid());
                        feedBackModel.setUserName(currentUser.getName());
                        feedBackModel.setFid(String.valueOf(System.currentTimeMillis()));
                      firebaseDatabaseHelper.sendFeedBack(feedBackModel,SendFeedBack.this);
                    }

                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onSendFeedBackDataCompleted(String models) {
        if(models.equals("success")){
            Toast.makeText(this, "FeedBack Send Successfully", Toast.LENGTH_SHORT).show();
            etSubject.setText("");
            etMessage.setText("");
        }else {
            Toast.makeText(this, "Error:"+models, Toast.LENGTH_SHORT).show();
        }

    }
}