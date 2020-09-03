package com.jamil.findme.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamil.findme.Activities.CreateNewUserAccount;
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
    ArrayList<Visitor> arrayList = new ArrayList<>();
    TextView tvNothingToShowUser;
    FloatingActionButton fbAddUser;

    public UsersFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(getActivity());
            prefs = new PreferencesManager(Objects.requireNonNull(getActivity()));
            currentUser = prefs.getCurrentUser();
            tvNothingToShowUser = view.findViewById(R.id.tvNothingToShowUser);
            fbAddUser = view.findViewById(R.id.fbAddUser);
            rvUsersList = view.findViewById(R.id.rvUsersList);
            rvUsersList.setHasFixedSize(true);
            rvUsersList.setLayoutManager(new LinearLayoutManager(view.getContext()));
            usersAdapter = new UsersAdapter(getActivity(), arrayList);
            rvUsersList.setAdapter(usersAdapter);
            rvUsersList.getRecycledViewPool().clear();
            usersAdapter.notifyDataSetChanged();
            arrayList.clear();
            firebaseDatabaseHelper.queryUsersByLocation(currentUser.getType(), currentUser.getLocation(), this);
            rvUsersList.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    if (dy < 0 && !fbAddUser.isShown())
                        fbAddUser.show();
                    else if (dy > 0 && fbAddUser.isShown())
                        fbAddUser.hide();
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                }
            });
            fbAddUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getContext(), CreateNewUserAccount.class);
                    startActivityForResult(i, 1);
                    usersAdapter.notifyDataSetChanged();
                    arrayList.clear();
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "onCreateView: UserFragment" + e.toString());
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        arrayList.clear();
        firebaseDatabaseHelper.queryUsersByLocation(currentUser.getType(), currentUser.getLocation(), this);
        usersAdapter.notifyDataSetChanged();
    }

    @Override
    public void onQueryUserByLocationComplete(ArrayList<Visitor> arrayListUsers) {
        if (arrayListUsers.size() < 1) {
            tvNothingToShowUser.setVisibility(View.VISIBLE);
            rvUsersList.setVisibility(View.GONE);
        } else {
            tvNothingToShowUser.setVisibility(View.GONE);
            rvUsersList.setVisibility(View.VISIBLE);
            arrayList.addAll(arrayListUsers);
            usersAdapter.notifyDataSetChanged();
            arrayListUsers.clear();
        }
    }
}