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
import com.jamil.findme.Models.ChatModel;
import com.jamil.findme.Models.FeedBackModel;
import com.jamil.findme.R;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;

public class FeedBackAdapter extends RecyclerView.Adapter<FeedBackAdapter.viewholder> {
private ArrayList<FeedBackModel> arrayListChast;
    private Context context;

    public FeedBackAdapter(ArrayList<FeedBackModel> arrayListChast, Context context) {
        this.arrayListChast = arrayListChast;
        this.context = context;
    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.single_item_report, parent, false);
        return new viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final FeedBackModel model = arrayListChast.get(position);
        try {
            holder.name.setText(model.getUserName());
            holder.email.setText(model.getEmail());
            holder.subject.setText(model.getSubject());
            holder.Message.setText(model.getMessage());

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
        TextView name,email,subject,Message;
        CircularImageView ivUserChat;


        public viewholder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.tvrpUserName);
            email = itemView.findViewById(R.id.tvrpUserEmail);
            subject = itemView.findViewById(R.id.tvrpSubject);
            Message = itemView.findViewById(R.id.tvrpMessage);
        }
    }
}
