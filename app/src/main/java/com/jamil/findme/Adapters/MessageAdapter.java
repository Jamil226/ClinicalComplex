package com.jamil.findme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import com.jamil.findme.Models.MessageModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.viewHolder> {
    ArrayList<MessageModel> messageModelArrayList = new ArrayList<>();
    Context context;
    private User currentUser;
    private PreferencesManager prefs;

    public MessageAdapter(ArrayList<MessageModel> messageModelArrayList, Context context) {
        this.messageModelArrayList = messageModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_message, parent, false);
        prefs = new PreferencesManager(context);
        currentUser = prefs.getCurrentUser();
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, int position) {
        MessageModel messageModel = messageModelArrayList.get(position);
        if (messageModel.getSenderName().toString().equals(currentUser.getName())) {
            holder.cvSender.setVisibility(View.VISIBLE);
            holder.cvReceiver.setVisibility(View.GONE);
            holder.tvSenderName.setText(messageModel.getSenderName());
            holder.tvSenderMessage.setText(messageModel.getMessage());
            holder.tvSenderDate.setText(messageModel.getTime());
        } else {
            holder.cvSender.setVisibility(View.GONE);
            holder.cvReceiver.setVisibility(View.VISIBLE);
            holder.tvReceiverName.setText(messageModel.getSenderName());
            holder.tvDate.setText(messageModel.getTime());
            holder.tvReceiverMessage.setText(messageModel.getMessage());
        }

    }

    @Override
    public int getItemCount() {
        return messageModelArrayList.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder {
        private TextView tvReceiverMessage, tvReceiverName, tvDate, tvSenderMessage, tvSenderName, tvSenderDate;
        private CardView cvSender, cvReceiver;


        public viewHolder(View view) {
            super(view);
            tvSenderMessage = view.findViewById(R.id.tvSenderMessage);
            tvSenderDate = view.findViewById(R.id.tvSenderDate);
            tvSenderName = view.findViewById(R.id.tvSenderName);
            tvReceiverMessage = view.findViewById(R.id.tvReceiverMessage);
            tvDate = view.findViewById(R.id.tvDate);
            tvReceiverName = view.findViewById(R.id.tvReceiverName);
            cvSender = view.findViewById(R.id.cvSender);
            cvReceiver = view.findViewById(R.id.cvReceiver);
        }
    }
}
