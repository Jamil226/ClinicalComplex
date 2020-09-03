package com.jamil.findme.Adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jamil.findme.Fragments.ChatFragment;
import com.jamil.findme.Fragments.MyProfileFragment;
import com.jamil.findme.Fragments.SparePartsFragment;
import com.jamil.findme.Fragments.UsersFragment;
import com.jamil.findme.Fragments.WorkShopsFragment;
import com.jamil.findme.Models.User;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.requireNonNull;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "TAG";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private Context context;
    private boolean isStudent;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    String type = null;

    public MainViewPagerAdapter(FragmentManager manager, Context context) {
        super(manager);
        DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
        PreferencesManager prefs = new PreferencesManager(requireNonNull(context));
        User currentUser = prefs.getCurrentUser();
        Log.e(TAG, "MainViewPagerAdapter: "
                + currentUser.getType());
        this.isStudent = isStudent;
        mFragmentList.add(new WorkShopsFragment());
        mFragmentTitleList.add("Service Providers");
        mFragmentList.add(new SparePartsFragment());
        mFragmentTitleList.add("Products");
        mFragmentList.add(new ChatFragment());
        mFragmentTitleList.add("Chat");
        if (mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")) {
            mFragmentList.add(new UsersFragment());
            mFragmentTitleList.add("User");
        }
        if (currentUser.getType().equals("WorkShop")) {
            mFragmentList.add(new MyProfileFragment());
            mFragmentTitleList.add("My Profile");
        }

    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

}
