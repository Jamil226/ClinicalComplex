package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;

import static androidx.constraintlayout.widget.Constraints.TAG;


public class ProposalFragment extends Fragment  {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvProposalList;
  //  private ArrayList<ProposalModel> arrayListProposal = new ArrayList<ProposalModel>();
    private User currentUser;
    private PreferencesManager prefs;
   // private ProposalAdapter proposalAdapter;
    private String catagory;
    private ProgressBar pbFragmentProposal;

    public ProposalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_proposal, container, false);


        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
        rvProposalList = view.findViewById(R.id.rvProposalList);
        pbFragmentProposal = view.findViewById(R.id.pbFragmentProposal);
        rvProposalList.setHasFixedSize(true);
        rvProposalList.addItemDecoration(new DividerItemDecoration(getActivity(), 0));
        rvProposalList.setLayoutManager(new LinearLayoutManager(view.getContext()));
      /*  proposalAdapter = new ProposalAdapter(arrayListProposal, getActivity(),"All");
        rvProposalList.setAdapter(proposalAdapter);
        rvProposalList.invalidate();
        proposalAdapter.notifyDataSetChanged();

        prefs = new PreferencesManager(getActivity());
        currentUser = prefs.getCurrentUser();
        if (currentUser instanceof Student) {
            catagory = "student";
            Log.e(TAG, "onCreateView: Its the student");
        } else {
            catagory = "supervisor";
            Toast.makeText(getActivity(), "Its Teacher", Toast.LENGTH_SHORT).show();
        }
        pbFragmentProposal.setVisibility(View.VISIBLE);
        firebaseDatabaseHelper.queryProposalData(currentUser.getUid(), catagory, this);
      */  return view;
    }



   /* @Override
    public void onProposalDataLoaded(ArrayList<ProposalModel> proposalList) {
        arrayListProposal.addAll(proposalList);
        pbFragmentProposal.setVisibility(View.GONE);
        proposalAdapter.notifyDataSetChanged();
        proposalList.clear();
    }*/
}
