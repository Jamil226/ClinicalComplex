package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jamil.findme.Adapters.ChatListAdapter;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;


public class ChatFragment extends Fragment implements FirebaseDatabaseHelper.OnQueryChatsDataCompleteListener {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvChatList;
    private ArrayList<ChatModel> arrayListChat = new ArrayList<ChatModel>();
    private User currentUser;
    private PreferencesManager prefs;
    private ChatListAdapter chatListAdapter;
    private static String TAG = "TAG";
    private String FilterString;
    TextView tvNoChats;

    public ChatFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        try {


            tvNoChats = view.findViewById(R.id.tvNoChats);
            rvChatList = view.findViewById(R.id.rvChatList);
            prefs = new PreferencesManager(getActivity());
            currentUser = prefs.getCurrentUser();
            rvChatList.setHasFixedSize(true);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
            rvChatList.setLayoutManager(new LinearLayoutManager(view.getContext()));
            chatListAdapter = new ChatListAdapter(arrayListChat, getActivity());
            rvChatList.setAdapter(chatListAdapter);
            chatListAdapter.notifyDataSetChanged();
            arrayListChat.clear();
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Its the Chat Exception.... " + e.toString());
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayListChat.clear();
        firebaseDatabaseHelper.queryChats(currentUser, this);
        chatListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onChattDataLoaded(ArrayList<ChatModel> studentlist) {
        if (studentlist.size() < 1) {
            tvNoChats.setVisibility(View.VISIBLE);
        } else {
            tvNoChats.setVisibility(View.GONE);
            rvChatList.setVisibility(View.VISIBLE);
            arrayListChat.addAll(studentlist);
            Log.e(TAG, "onChatDataLoaded: " + arrayListChat);
            chatListAdapter.notifyDataSetChanged();
            studentlist.clear();
        }
    }
}
