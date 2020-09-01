package com.jamil.findme.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Activities.EditWorkShopInfo;
import com.jamil.findme.Activities.WorkShopProfile;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;

public class WorkShopsAdapter extends RecyclerView.Adapter<WorkShopsAdapter.viewholder> {
    private ArrayList<WorkShopModel> arrayListProposal;
    private Context context;
    private User currentUser;
    private PreferencesManager prefs;
    private String filter;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;

    public WorkShopsAdapter(ArrayList<WorkShopModel> arrayListProposal, Context context, String filter) {
        this.arrayListProposal = arrayListProposal;
        this.context = context;
        this.filter = filter;
    }

    public WorkShopsAdapter() {

    }

    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.single_item_workshop, parent, false);
        prefs = new PreferencesManager(context);
        currentUser = prefs.getCurrentUser();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(context);
        return new viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder, int position) {
        final WorkShopModel model = arrayListProposal.get(position);
        holder.tvWorkShopName.setText(model.getWorkShopName());
        holder.tvWorkShopDec.setText(model.getDescription());
        holder.tWorkShopLocation.setText(model.getLocation());
        Glide.with(context).load(model.getImage()).into(holder.ivWorkShoplist);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, WorkShopProfile.class);
                intent.putExtra("WSP", new Gson().toJson(model));
                context.startActivity(intent);
                arrayListProposal.clear();
            }
        });
        if (currentUser.getType().equals("Admin")) {
            holder.llEditUserOptionWorkShop.setVisibility(View.VISIBLE);
        } else {
            holder.llEditUserOptionWorkShop.setVisibility(View.GONE);

        }
        holder.icEditAccountWSList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditWorkShopInfo.class);
                intent.putExtra("WS", new Gson().toJson(model));
                context.startActivity(intent);
                arrayListProposal.clear();
            }
        });
        holder.icDelAccountWorkShopList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_delete)
                        .setTitle("Delete Work Shop")
                        .setMessage("Are you sure you want to Delete User.?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseDatabaseHelper.
                                        deleteUserById(model.getUid());
                                arrayListProposal.clear();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });

    }


    @Override
    public int getItemCount() {

        return arrayListProposal.size();
    }

    static class viewholder extends RecyclerView.ViewHolder {
        TextView tvWorkShopName, tWorkShopLocation, tvWorkShopDec;
        CircularImageView ivWorkShoplist;
        ImageView icDelAccountWorkShopList, icEditAccountWSList;
LinearLayout llEditUserOptionWorkShop;
        viewholder(@NonNull View v) {
            super(v);
            icDelAccountWorkShopList = v.findViewById(R.id.icDelAccountWorkShopList);
            llEditUserOptionWorkShop = v.findViewById(R.id.llEditUserOptionWorkShop);
            icEditAccountWSList = v.findViewById(R.id.icEditAccountWSList);
            ivWorkShoplist = v.findViewById(R.id.ivWorkShoplist);
            tWorkShopLocation = v.findViewById(R.id.tWorkShopLocation);
            tvWorkShopDec = v.findViewById(R.id.tvWorkShopDec);
            tvWorkShopName = v.findViewById(R.id.tvNameWorkShoplist);

        }
    }

}
