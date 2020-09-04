package com.jamil.findme.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.jamil.findme.Models.GeneralRepairModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class GeneralRepairAdapter extends RecyclerView.Adapter<GeneralRepairAdapter.ViewHolder> {
    private ArrayList<GeneralRepairModel> arrayList;
    private String TAG = "TAG";
    private Context context;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private User currentUser;
    private PreferencesManager prefs;

    public GeneralRepairAdapter(ArrayList<GeneralRepairModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public GeneralRepairAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_general_repair, parent, false);
        context = parent.getContext();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(context);
        prefs = new PreferencesManager(context);
        currentUser = prefs.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GeneralRepairAdapter.ViewHolder holder, int position) {
        final GeneralRepairModel postModel = arrayList.get(position);
        Glide.with(context).load(postModel.getImage()).into(holder.ivPost);
        holder.tvpNameUser.setText(postModel.getUsername());
        holder.desc.setText(postModel.getDescription());
        holder.tvTitle.setText(postModel.getTitle());
        holder.tvdate.setText(postModel.getDate());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc, tvpNameUser, tvdate,tvTitle;
        ImageView ivPost;

        public ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvpNameUser = view.findViewById(R.id.tvBlogUsername);
            tvdate = view.findViewById(R.id.tvDateGR);
            ivPost = view.findViewById(R.id.ivBlogPost);
            desc = view.findViewById(R.id.tvPostDescription);
        }
    }
}
