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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.jamil.findme.Activities.EditVisitorInfoActivity;
import com.jamil.findme.Activities.UserProfileActivity;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.viewholder> {
    private List<Visitor> arrayList;
    private Context context;
    User currentUser;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    PreferencesManager pref;

    public UsersAdapter(Context context, List<Visitor> arrayList) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    public viewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.signle_item_users, viewGroup, false);
        pref = new PreferencesManager(context);
        currentUser = pref.getCurrentUser();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(context);
        return new viewholder(v);
    }

    public void onBindViewHolder(@NonNull final viewholder holder, final int position) {
        final Visitor model = arrayList.get(position);
        holder.location.setText(String.valueOf(model.getLocation()));
        holder.Name.setText(model.getName());
        holder.email.setText(model.getEmail());
        holder.tvPhoneUserlist.setText(model.getPhone());
        Glide.with(context).load(model.getImage()).into(holder.image);
        if (currentUser.getType().equals("Admin")) {
            holder.llEditUserOption.setVisibility(View.VISIBLE);
        } else {
            holder.llEditUserOption.setVisibility(View.GONE);

        }
        holder.icEditAccountUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, EditVisitorInfoActivity.class);
                intent.putExtra("UID", new Gson().toJson(model));
                context.startActivity(intent);
                arrayList.clear();
            }
        });
        holder.icDelAccountUserList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(context)
                        .setIcon(R.drawable.ic_delete)
                        .setTitle("Delete User")
                        .setMessage("Are you sure you want to Delete User.?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                firebaseDatabaseHelper.deleteUserById(model.getUid());
                                arrayList.remove(position);
                                notifyDataSetChanged();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserProfileActivity.class);
                intent.putExtra("UID", new Gson().toJson(model));
                context.startActivity(intent);
            }
        });
    }


    public int getItemCount() {
        return arrayList.size();
    }

    static class viewholder extends RecyclerView.ViewHolder {
        TextView Name, email, location, tvPhoneUserlist;
        CircularImageView image;
        ImageView icEditAccountUserList, icDelAccountUserList;
        LinearLayout llEditUserOption;

        viewholder(@NonNull View v) {
            super(v);
            icEditAccountUserList = v.findViewById(R.id.icEditAccountUserList);
            icDelAccountUserList = v.findViewById(R.id.icDelAccountUserList);
            llEditUserOption = v.findViewById(R.id.llEditUserOption);
            image = v.findViewById(R.id.ivUserslist);
            Name = v.findViewById(R.id.tvNameUserslist);
            email = v.findViewById(R.id.tvEmailUserlist);
            location = v.findViewById(R.id.tvLocationUserList);
            tvPhoneUserlist = v.findViewById(R.id.tvPhoneUserlist);

        }
    }

}
