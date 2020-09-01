package com.jamil.findme.Adapters;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.jamil.findme.Fragments.ChatFragment;
import com.jamil.findme.Fragments.WorkShopsFragment;
import com.jamil.findme.Fragments.SparePartsFragment;
import com.jamil.findme.Fragments.UsersFragment;
import com.jamil.findme.Models.User;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;

public class MainViewPagerAdapter extends FragmentPagerAdapter {
    private static final String TAG = "TAG";
    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();
  private   Context context;
    private boolean isStudent;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser user = mAuth.getCurrentUser();
    /*private PreferencesManager prefs=new PreferencesManager(requireNonNull(context));
    private User currentUser=prefs.getCurrentUser();*/

    public MainViewPagerAdapter(FragmentManager manager) {
        super(manager);

        this.isStudent = isStudent;
        mFragmentList.add(new WorkShopsFragment());
        mFragmentTitleList.add("Work Shops");
        mFragmentList.add(new ChatFragment());
        mFragmentTitleList.add("Chat");
        mFragmentList.add(new SparePartsFragment());
        mFragmentTitleList.add("Spare Parts");
        if(mAuth.getCurrentUser().getEmail().equals("admin@gmail.com")){
        mFragmentList.add(new UsersFragment());
        mFragmentTitleList.add("User");}


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
