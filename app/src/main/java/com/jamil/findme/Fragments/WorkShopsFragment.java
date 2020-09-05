package com.jamil.findme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamil.findme.Activities.caNewWorkShop;
import com.jamil.findme.Adapters.WorkShopsAdapter;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;


public class WorkShopsFragment extends Fragment implements FirebaseDatabaseHelper.onQueryWorkShopDataCompleteListener {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvProposalList;
    private ArrayList<WorkShopModel> arrayListProposal = new ArrayList<WorkShopModel>();
    private User currentUser;
    private PreferencesManager prefs;
    private WorkShopsAdapter workShopsAdapter;
    private String catagory;
    private ProgressBar pbFragmentProposal;
    TextView tvNothingToShow;
    FloatingActionButton fbAddWorkShop;

    public WorkShopsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workshop, container, false);


        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
        prefs = new PreferencesManager(getActivity());
        currentUser = prefs.getCurrentUser();
        fbAddWorkShop = view.findViewById(R.id.fbAddWorkShop);
        tvNothingToShow = view.findViewById(R.id.tvNothingToShow);
        rvProposalList = view.findViewById(R.id.rvWorkShopList);
        pbFragmentProposal = view.findViewById(R.id.pbFragmentProposal);
        pbFragmentProposal.setVisibility(View.VISIBLE);
        rvProposalList.setHasFixedSize(true);
        rvProposalList.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        rvProposalList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        workShopsAdapter = new WorkShopsAdapter(arrayListProposal, getActivity(), "All");
        rvProposalList.setAdapter(workShopsAdapter);
        arrayListProposal.clear();
        workShopsAdapter.notifyDataSetChanged();
        firebaseDatabaseHelper.queryWorkShopData(currentUser.getUid(), currentUser.getType(), this);
        fbAddWorkShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getContext(), caNewWorkShop.class);
                startActivityForResult(i, 2);
                arrayListProposal.clear();
                workShopsAdapter.notifyDataSetChanged();
            }
        });
        if (currentUser.getType().equals("Admin")) {
            fbAddWorkShop.setVisibility(View.VISIBLE);
        } else {
            fbAddWorkShop.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onQueryWorkShopDataCompleteListener(ArrayList<WorkShopModel> models) {
        if (models.size() < 1) {
            tvNothingToShow.setVisibility(View.VISIBLE);
            pbFragmentProposal.setVisibility(View.GONE);
            rvProposalList.setVisibility(View.GONE);
        } else {
            Collections.reverse(models);
            tvNothingToShow.setVisibility(View.GONE);
            rvProposalList.setVisibility(View.VISIBLE);
            arrayListProposal.addAll(models);
            pbFragmentProposal.setVisibility(View.GONE);
            workShopsAdapter.notifyDataSetChanged();
            models.clear();
        }
    }
}
