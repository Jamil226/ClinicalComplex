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
import com.jamil.findme.Models.PostModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;

public class SparePartsAdapter extends RecyclerView.Adapter<SparePartsAdapter.ViewHolder> {
    private ArrayList<PostModel> arrayList;
    private String TAG = "TAG";
    private Context context;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private User currentUser;
    private PreferencesManager prefs;

    public SparePartsAdapter(ArrayList<PostModel> arrayList) {
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public SparePartsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_item_spareparts, parent, false);
        context = parent.getContext();
        firebaseDatabaseHelper = new FirebaseDatabaseHelper(context);
        prefs = new PreferencesManager(context);
        currentUser = prefs.getCurrentUser();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SparePartsAdapter.ViewHolder holder, int position) {
        final PostModel postModel = arrayList.get(position);
        Glide.with(context).load(postModel.getImage()).into(holder.ivspitem);
        holder.tvpNamespitem.setText(postModel.getProductName());
        holder.tvpricespitem.setText("$"+postModel.getPrice());
        holder.tvWorkShopspitem.setText(postModel.getWorkShop());
        holder.desc.setText(postModel.getDescription());
        holder.tvpTypespitem.setText(postModel.getType());
        holder.tvModelspitem.setText(postModel.getModel());

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView desc, tvpNamespitem, tvpricespitem, tvWorkShopspitem, tvpTypespitem, tvModelspitem;
        ImageView ivspitem;

        public ViewHolder(View view) {
            super(view);
            tvModelspitem = view.findViewById(R.id.tvModelspitem);
            tvpTypespitem = view.findViewById(R.id.tvpTypespitem);
            tvWorkShopspitem = view.findViewById(R.id.tvWorkShopspitem);
            tvpricespitem = view.findViewById(R.id.tvpricespitem);
            tvpNamespitem = view.findViewById(R.id.tvpNamespitem);
            ivspitem = view.findViewById(R.id.ivspitem);
            desc = view.findViewById(R.id.desc);
        }
    }
}
