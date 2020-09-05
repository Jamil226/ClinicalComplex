package com.jamil.findme.Activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Adapters.MessageAdapter;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.MessageModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ChatActivity extends AppCompatActivity implements FirebaseDatabaseHelper.OnQueryMessagesDataCompleteListener {
    private static final String TAG = "TAG";
    private ImageView ivBack;
    private ArrayList<MessageModel> arrayList = new ArrayList<>();
    private User currentUser;
    private PreferencesManager prefs;
    private ChatModel chatModel;
    private TextView tvChatName;
    private RecyclerView rvChatActivity;
    private EditText etMessage;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private ImageView ivSendMessage;
    private MessageAdapter messageAdapter;
    CircularImageView ivUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        try {
            chatModel = new Gson().fromJson(getIntent().getStringExtra("CID"), ChatModel.class);
            ivUser = findViewById(R.id.ivUser);
            ivBack = findViewById(R.id.ivBack);
            tvChatName = findViewById(R.id.tvChatName);
            etMessage = findViewById(R.id.etMessage);
            ivSendMessage = findViewById(R.id.ivSendMessage);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(ChatActivity.this);
            //adapter
            rvChatActivity = findViewById(R.id.rvChatActivity);
            rvChatActivity.setHasFixedSize(true);
            rvChatActivity.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
            messageAdapter = new MessageAdapter(arrayList, ChatActivity.this);
            rvChatActivity.setAdapter(messageAdapter);
            arrayList.clear();
            messageAdapter.notifyDataSetChanged();
            Glide.with(this).load(chatModel.getImage()).into(ivUser);
            tvChatName.setText(chatModel.getName());
            ivSendMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(etMessage.getText())) {
                        etMessage.setError("please enter your message");
                        etMessage.requestFocus();
                    } else {
                        String mess = etMessage.getText().toString().trim();
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        String date = df.format(Calendar.getInstance().getTime());
                        MessageModel messageModel = new MessageModel();
                        messageModel.setMessage(mess);
                        messageModel.setMessageId(System.currentTimeMillis() + currentUser.getName());
                        messageModel.setSenderName(currentUser.getName());
                        messageModel.setTime(date);
                        firebaseDatabaseHelper.sendMessage(messageModel, chatModel.getId());
                        etMessage.setText("");
                        arrayList.clear();
                    }
                }
            });

            ivBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }

    @Override
    public void onMessagesDataLoaded(ArrayList<MessageModel> messageModelArrayList) {
        arrayList.addAll(messageModelArrayList);
        messageAdapter.notifyDataSetChanged();
        rvChatActivity.scrollToPosition(arrayList.size() - 1);
        messageModelArrayList.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: Starting Activity CHat");
        firebaseDatabaseHelper.queryMessages(chatModel.getId(), this);

    }

}
