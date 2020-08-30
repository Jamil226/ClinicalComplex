package com.jamil.findme.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jamil.findme.Adapters.UsersAdapter;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.Objects;


public class UsersFragment extends Fragment implements FirebaseDatabaseHelper.onQueryUserByLocationCompleteListener {
    public static final String TAG = "TAG";
    RecyclerView rvUsersList;
    private User currentUser;
    private PreferencesManager prefs;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    UsersAdapter usersAdapter;
    ArrayList<Visitor> arrayList;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        try {
            arrayList = new ArrayList<>();
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
            prefs = new PreferencesManager(Objects.requireNonNull(getActivity()));
            currentUser = prefs.getCurrentUser();
            rvUsersList = view.findViewById(R.id.rvUsersList);
            rvUsersList.setHasFixedSize(true);
            rvUsersList.setLayoutManager(new LinearLayoutManager(view.getContext()));
            usersAdapter = new UsersAdapter(getActivity(), arrayList);
            rvUsersList.setAdapter(usersAdapter);
            arrayList.clear();
            firebaseDatabaseHelper.queryUsersByLocation(currentUser.getLocation(), this);
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: UserFragment" + e.toString());
        }

        return view;
    }

    @Override
    public void onQueryUserByLocationComplete(ArrayList<Visitor> arrayListUsers) {
        arrayList.addAll(arrayListUsers);
        // pbFragmentSupervisor.setVisibility(View.GONE);
        usersAdapter.notifyDataSetChanged();
        arrayListUsers.clear();
    }
}