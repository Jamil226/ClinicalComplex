package com.jamil.findme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamil.findme.Activities.AddSparePart;
import com.jamil.findme.Adapters.SparePartsAdapter;
import com.jamil.findme.Models.PostModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;


public class SparePartsFragment extends Fragment implements FirebaseDatabaseHelper.onQuerySparePartsDataCompleteListener {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvSparePartsList;
    private ArrayList<PostModel> arrayList = new ArrayList<>();
    private User currentUser;
    private PreferencesManager prefs;
    private ProgressBar pbFragmentSupervisor;
    FloatingActionButton fbAddSparePart;
    SparePartsAdapter sparePartsAdapter;
    private TextView tvNoData;

    public SparePartsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_spare_parts, container, false);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
        prefs = new PreferencesManager(getActivity());
        currentUser = prefs.getCurrentUser();
        tvNoData = view.findViewById(R.id.tvNoData);
        fbAddSparePart = view.findViewById(R.id.fbAddSparePart);
        fbAddSparePart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent= new Intent(getActivity(), AddSparePart.class);
               startActivityForResult(intent,3);
                sparePartsAdapter.notifyDataSetChanged();
                arrayList.clear();

            }
        });
        if(currentUser.getType().equals("Admin")){
            fbAddSparePart.setVisibility(View.VISIBLE);
        }else{
            fbAddSparePart.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Else Case", Toast.LENGTH_LONG).show();
        }
        rvSparePartsList = view.findViewById(R.id.rvSparePartsList);
        rvSparePartsList.setHasFixedSize(true);
        rvSparePartsList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        sparePartsAdapter = new SparePartsAdapter(arrayList);
        rvSparePartsList.setAdapter(sparePartsAdapter);
        rvSparePartsList.setItemViewCacheSize(10);
        arrayList.clear();
        sparePartsAdapter.notifyDataSetChanged();
        firebaseDatabaseHelper.querySparePartsData(this);

        return view;
    }

    @Override
    public void onSparePartsDataCompleted(ArrayList<PostModel> models) {
        if (models.size() < 1) {
            tvNoData.setVisibility(View.VISIBLE);
            rvSparePartsList.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "No Data Found", Toast.LENGTH_SHORT).show();
        } else {
            Collections.reverse(models);
            arrayList.addAll(models);
            sparePartsAdapter.notifyDataSetChanged();
            models.clear();
            tvNoData.setVisibility(View.GONE);
            rvSparePartsList.setVisibility(View.VISIBLE);
        }
    }

/*
    @Override
    public void onSupervisorDataLoaded(ArrayList<Supervisor> supervisorsList) {
        arrayList.addAll(supervisorsList);
        pbFragmentSupervisor.setVisibility(View.GONE);
       // supervisorAdapter.notifyDataSetChanged();
        supervisorAdapter.notifyItemInserted(arrayList.size());
    }
*/
}
