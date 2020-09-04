package com.jamil.findme.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.jamil.findme.Adapters.MainViewPagerAdapter;
import com.jamil.findme.Models.User;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;
import com.mikhaellopez.circularimageview.CircularImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "TAG";
    private User currentUser;
    private PreferencesManager prefs;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MainViewPagerAdapter viewPagerAdapter;
    FirebaseDatabaseHelper firebaseDatabaseHelper;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        try {
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(MainActivity.this);
            setupProgressDialog();
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();

            Toolbar toolbar = findViewById(R.id.toolbarMain);
            setSupportActionBar(toolbar);
            setUserProfile();

            viewPager = findViewById(R.id.viewpager);
            viewPager.setOffscreenPageLimit(3);
            viewPagerAdapter = new MainViewPagerAdapter(getSupportFragmentManager(), this);
            viewPager.setAdapter(viewPagerAdapter);
            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            DrawerLayout drawer = findViewById(R.id.drawer_layout);
            NavigationView navigationView = findViewById(R.id.nav_view);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer,
                    toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();
            navigationView.setNavigationItemSelectedListener(this);


        } catch (Exception e) {
            Log.e(TAG, "onCreate: Main " + e.toString());
        }
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setTitle("Loading.");
        progressDialog.setMessage("Please wait");
        progressDialog.setCancelable(false);
    }

    private void setUserProfile() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerLayout = navigationView.getHeaderView(0);
        ((TextView) headerLayout.findViewById(R.id.tvNavName)).setText(currentUser.getName());
        ((TextView) headerLayout.findViewById(R.id.tvNavEmail)).setText(currentUser.getEmail());
        final CircularImageView userImage = headerLayout.findViewById(R.id.ivNavUser);
        Glide.with(headerLayout).load(currentUser.getImage()).into(userImage);
        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!currentUser.getType().equals("WorkShop")) {
                    Visitor visitor = new Visitor();
                    visitor.setUid(currentUser.getUid());
                    visitor.setName(currentUser.getName());
                    visitor.setEmail(currentUser.getEmail());
                    visitor.setImage(currentUser.getImage());
                    visitor.setPhone(currentUser.getPhone());
                    visitor.setLocation(currentUser.getLocation());
                    visitor.setType(currentUser.getType());
                    visitor.setPassword(currentUser.getPassword());

                    Intent intent = new Intent(MainActivity.this, UserProfileActivity.class);
                    intent.putExtra("UID", new Gson().toJson(visitor));
                    startActivity(intent);
                } else {
                    progressDialog.show();
                    final DatabaseReference tableUser = FirebaseDatabase.getInstance().getReference().child("Users");
                    tableUser.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.e(TAG, "onDataChange: Snapshot" + dataSnapshot);
                            WorkShopModel workShopModel = dataSnapshot.getValue(WorkShopModel.class);
                            Intent intent = new Intent(MainActivity.this, WorkShopProfile.class);
                            intent.putExtra("WSP", new Gson().toJson(workShopModel));
                            startActivity(intent);
                            tableUser.removeEventListener(this);
                            progressDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Error " + databaseError, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        switch (id) {
            case R.id.nav_supervisors:
                viewPager.setCurrentItem(0, true);
                break;
            case R.id.nav_products:
                viewPager.setCurrentItem(1, true);
                break;
            case R.id.nav_chat:
                viewPager.setCurrentItem(2, true);
                break;

            case R.id.nav_about_us: {
                startActivity(new Intent(MainActivity.this, ActivityAbout.class));
            }
            break;
            case R.id.nav_repair: {
                startActivity(new Intent(MainActivity.this
                        ,GenaeralPepair.class));
            }
            break;
            case R.id.nav_vehicle: {
                startActivity(new Intent(MainActivity.this
                        , ActivityVehicleMaintenance.class));
            }
            break;
            case R.id.nav_settings: {
                if (currentUser.getType().equals("Admin")) {
                    startActivity(new Intent(MainActivity.this, FeedBackList.class));
                } else {
                    startActivity(new Intent(MainActivity.this, SendFeedBack.class));
                }
            }
            break;
            case R.id.nav_logout: {
                logout();
            }
            break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Logout:
                logout();
                return true;
            default:
                return false;
        }

    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(MainActivity.this, LogInActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (viewPager.getCurrentItem() != 0) {
            viewPager.setCurrentItem(0, true);
        } else {
            super.onBackPressed();
        }
    }

}