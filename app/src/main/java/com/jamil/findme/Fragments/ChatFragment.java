package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;


public class ChatFragment extends Fragment {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvChatList;
  //  private ArrayList<ChatModel> arrayListChat = new ArrayList<ChatModel>();
    private User currentUser;
    private PreferencesManager prefs;
    //private ChatListAdapter chatListAdapter;
    private static String TAG = "TAG";
    private String FilterString;

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


           // rvChatList = view.findViewById(R.id.rvChatList);
            prefs = new PreferencesManager(getActivity());
            currentUser = prefs.getCurrentUser();
          /*  rvChatList.setHasFixedSize(true);
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
          // rvChatList.setLayoutManager(new LinearLayoutManager(view.getContext()));
           /* chatListAdapter = new ChatListAdapter(arrayListChat, getActivity());
            rvChatList.setAdapter(chatListAdapter);
            chatListAdapter.notifyDataSetChanged();
            arrayListChat.clear();
            if (currentUser instanceof Student) {
                FilterString = "participants";
            } else {
                FilterString = "supervisors";
            }
            firebaseDatabaseHelper.queryChats(FilterString, currentUser.getEmail(), this);
*/
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: Its the Chat Exception.... " + e.toString());
        }
        return view;
    }
/*

    @Override
    public void onChattDataLoaded(ArrayList<ChatModel> studentlist) {
        arrayListChat.addAll(studentlist);
        Log.e(TAG, "onChatDataLoaded: " + arrayListChat);
        chatListAdapter.notifyDataSetChanged();
        studentlist.clear();
    }
*/
}
