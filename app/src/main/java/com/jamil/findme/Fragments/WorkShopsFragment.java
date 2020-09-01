package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamil.findme.Adapters.WorkShopsAdapter;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;


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
        workShopsAdapter.notifyDataSetChanged();
        arrayListProposal.clear();
        firebaseDatabaseHelper.queryWorkShopData(currentUser.getUid(), currentUser.getType(), this);
        fbAddWorkShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Working", Toast.LENGTH_SHORT).show();
            }
        });
        rvProposalList.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy < 0 && !fbAddWorkShop.isShown())
                    fbAddWorkShop.show();
                else if (dy > 0 && fbAddWorkShop.isShown())
                    fbAddWorkShop.hide();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });

        return view;
    }

    @Override
    public void onQueryWorkShopDataCompleteListener(ArrayList<WorkShopModel> models) {
        if (models.size() < 1) {
            tvNothingToShow.setVisibility(View.VISIBLE);
            pbFragmentProposal.setVisibility(View.GONE);
            rvProposalList.setVisibility(View.GONE);
        } else {
            tvNothingToShow.setVisibility(View.GONE);
            rvProposalList.setVisibility(View.VISIBLE);
            arrayListProposal.addAll(models);
            pbFragmentProposal.setVisibility(View.GONE);
            workShopsAdapter.notifyDataSetChanged();
            models.clear();
        }
    }



   /* @Override
    public void onProposalDataLoaded(ArrayList<ProposalModel> proposalList) {
        arrayListProposal.addAll(proposalList);
        pbFragmentProposal.setVisibility(View.GONE);
        proposalAdapter.notifyDataSetChanged();
        proposalList.clear();
    }*/
}
