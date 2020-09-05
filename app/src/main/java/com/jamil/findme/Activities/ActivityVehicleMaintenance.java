package com.jamil.findme.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jamil.findme.Adapters.GeneralRepairAdapter;
import com.jamil.findme.Models.GeneralRepairModel;
import com.jamil.findme.Models.User;
import com.jamil.findme.R;
import com.jamil.findme.Utilities.FirebaseDatabaseHelper;
import com.jamil.findme.Utilities.PreferencesManager;

import java.util.ArrayList;
import java.util.Collections;

public class ActivityVehicleMaintenance extends AppCompatActivity implements FirebaseDatabaseHelper.onGeneralVehicleMaintenanceDataCompleteListener {
    private static final String TAG ="TAG" ;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvGeneralItemList;
    private ArrayList<GeneralRepairModel> arrayList = new ArrayList<>();
    private User currentUser;
    private PreferencesManager prefs;
    FloatingActionButton fbAddArticle;
    GeneralRepairAdapter generalRepairAdapter;
    private TextView tvNoData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_maintenance);
        try{
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            tvNoData = findViewById(R.id.tvNoData);
            fbAddArticle = findViewById(R.id.fbAddArticle);
            fbAddArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(ActivityVehicleMaintenance.this, AddGeneralVehicleMaintenanceItem.class);
                    startActivityForResult(intent,5);
                    generalRepairAdapter.notifyDataSetChanged();
                    arrayList.clear();

                }
            });
            if(currentUser.getType().equals("Admin")){
                fbAddArticle.setVisibility(View.VISIBLE);
            }else{
                fbAddArticle.setVisibility(View.GONE);
            }
            rvGeneralItemList = findViewById(R.id.rvGeneralItemList);
            rvGeneralItemList.setHasFixedSize(true);
            rvGeneralItemList.setLayoutManager(new LinearLayoutManager(this));
            generalRepairAdapter = new GeneralRepairAdapter(arrayList);
            rvGeneralItemList.setAdapter(generalRepairAdapter);
            rvGeneralItemList.setItemViewCacheSize(10);
            arrayList.clear();
            generalRepairAdapter.notifyDataSetChanged();
            firebaseDatabaseHelper.queryGeneralVehicleMaintenanceData(this);

        }catch (Exception e)
        {
            Log.e(TAG, "onCreate: "+e.toString() );
        }

    }

    @Override
    public void onGeneralVehicleMaintenanceDataCompleted(ArrayList<GeneralRepairModel> models) {
        if (models.size() < 1) {
            tvNoData.setVisibility(View.VISIBLE);
            rvGeneralItemList.setVisibility(View.INVISIBLE);
        } else {
            Collections.reverse(models);
            arrayList.addAll(models);
            generalRepairAdapter.notifyDataSetChanged();
            models.clear();
            tvNoData.setVisibility(View.GONE);
            rvGeneralItemList.setVisibility(View.VISIBLE);
        }
    }
}