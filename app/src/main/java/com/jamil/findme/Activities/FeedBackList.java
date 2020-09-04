package com.jamil.findme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.jamil.findme.Adapters.ChatListAdapter;
import com.jamil.findme.Adapters.FeedBackAdapter;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.FeedBackModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;

public class FeedBackList extends AppCompatActivity implements FirebaseDatabaseHelper.onRetrieveFeedBackDataCompleteListener {

    private static final String TAG ="TAG" ;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvChatList;
    private ArrayList<FeedBackModel> arrayListChat = new ArrayList<FeedBackModel>();
    private User currentUser;
    private PreferencesManager prefs;
    private FeedBackAdapter chatListAdapter;
    TextView tvNoChats;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back_list);

        try {
            tvNoChats = findViewById(R.id.tvNoChats);
            rvChatList = findViewById(R.id.rvFeedBacKList);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            rvChatList.setHasFixedSize(true);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            rvChatList.setLayoutManager(new LinearLayoutManager(this));
            chatListAdapter = new FeedBackAdapter(arrayListChat, this);
            rvChatList.setAdapter(chatListAdapter);
            chatListAdapter.notifyDataSetChanged();
            arrayListChat.clear();
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Its the Chat Exception.... " + e.toString());
        }
    }
    @Override
    public void onStart() {
        super.onStart();
        arrayListChat.clear();
        firebaseDatabaseHelper.getFeedBacks( this);
        chatListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onRetrieveFeedBackDataCompleted(ArrayList<FeedBackModel> models) {
        if (models.size() < 1) {
            tvNoChats.setVisibility(View.VISIBLE);
            Toast.makeText(this, "No Data", Toast.LENGTH_SHORT).show();
        } else {
            tvNoChats.setVisibility(View.GONE);
            rvChatList.setVisibility(View.VISIBLE);
            arrayListChat.addAll(models);
            Log.e(TAG, "onChatDataLoaded: " + arrayListChat);
            chatListAdapter.notifyDataSetChanged();
            models.clear();
        }
    }
}