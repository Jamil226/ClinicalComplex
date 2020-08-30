package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.List;


public class SupervisorFragment extends Fragment {
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvSupervisorList;
    private List<WorkShopModel> arrayList = new ArrayList<>();
    private User currentUser;
    private PreferencesManager prefs;
    private ProgressBar pbFragmentSupervisor;

    public SupervisorFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_supervisor, container, false);

        firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
/*
        rvSupervisorList = view.findViewById(R.id.rvSupervisorList);
        pbFragmentSupervisor = view.findViewById(R.id.pbFragmentSupervisor);
        rvSupervisorList.setHasFixedSize(true);
        rvSupervisorList.setLayoutManager(new LinearLayoutManager(view.getContext()));
        supervisorAdapter = new SupervisorAdapter(getActivity(), arrayList);
        rvSupervisorList.setAdapter(supervisorAdapter);

        prefs = new PreferencesManager(getActivity());
        currentUser = prefs.getCurrentUser();
        pbFragmentSupervisor.setVisibility(View.VISIBLE);
        firebaseDatabaseHelper.querySupervisorData(currentUser.getCampus(), currentUser.getDepartment(), this);
*/

        return view;
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
