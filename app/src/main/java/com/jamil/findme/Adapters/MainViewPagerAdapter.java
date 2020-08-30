package com.jamil.findme.Adapters;

import android.content.Context;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.jamil.findme.Fragments.ChatFragment;
import com.jamil.findme.Fragments.ProposalFragment;
import com.jamil.findme.Fragments.SupervisorFragment;
import com.jamil.findme.Fragments.UsersFragment;
import com.jamil.findme.Models.User;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "TAG";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
    private boolean isStudent;
    private PreferencesManager prefs;
    private User currentUser;
    Context context;

    public MainViewPagerAdapter(FragmentManager manager) {
        super(manager);

        this.isStudent = isStudent;
        mFragmentList.add(new SupervisorFragment());
        mFragmentTitleList.add("Work Shops");
        mFragmentList.add(new ChatFragment());
        mFragmentTitleList.add("Chat");
        mFragmentList.add(new ProposalFragment());
        mFragmentTitleList.add("Spare Parts");
        mFragmentList.add(new UsersFragment());
        mFragmentTitleList.add("User");


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
