package com.jamil.findme.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Activities.ChatActivity;
import com.jamil.findme.Activities.WorkShopProfile;
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import static androidx.constraintlayout.widget.Constraints.TAG;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.viewholder> {
    private ArrayList<ChatModel> arrayListChast;
    private Context context;

    public ChatListAdapter(ArrayList<ChatModel> arrayListChast, Context context) {
        this.arrayListChast = arrayListChast;
        this.context = context;
    }



    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat_list, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final ChatModel model =arrayListChast.get(position);
        try {
            holder.tvChatList.setText(model.getName());
            Glide.with(context).load(model.getImage()).into(holder.ivUserChat);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,
                            ChatActivity.class);
                    intent.putExtra("CID",new Gson().toJson(model));
                   context.startActivity(intent);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onBindViewHolder: " + e.toString());
        }
        //holder.tvChatMessage.setText(String.valueOf(model.getParticipants().size()));
    }

    @Override
    public int getItemCount() {
        return arrayListChast.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        TextView tvChatList, tvChatMessage;
        CircularImageView ivUserChat;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            tvChatList = itemView.findViewById(R.id.tvUserNameChatList);
            ivUserChat = itemView.findViewById(R.id.ivUserChat);
            tvChatMessage = itemView.findViewById(R.id.tvChatMessage);
        }
    }
}
