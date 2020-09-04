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

public class GenaeralPepair extends AppCompatActivity implements FirebaseDatabaseHelper.onGeneralRepairArticlesDataCompleteListener {

    private static final String TAG ="TAG" ;
    private FirebaseDatabaseHelper firebaseDatabaseHelper;
    private RecyclerView rvSparePartsList;
    private ArrayList<GeneralRepairModel> arrayList = new ArrayList<>();
    private User currentUser;
    private PreferencesManager prefs;
    private ProgressBar pbFragmentSupervisor;
    FloatingActionButton fbAddArticle;
    GeneralRepairAdapter generalRepairAdapter;
    private TextView tvNoData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genaeral_pepair);
        try{
            firebaseDatabaseHelper = new FirebaseDatabaseHelper(this);
            prefs = new PreferencesManager(this);
            currentUser = prefs.getCurrentUser();
            tvNoData = findViewById(R.id.tvNoData);
            fbAddArticle = findViewById(R.id.fbAddArticle);
            fbAddArticle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent= new Intent(GenaeralPepair.this, AddGeneralRepairPost.class);
                    startActivityForResult(intent,4);
                    generalRepairAdapter.notifyDataSetChanged();
                    arrayList.clear();

                }
            });
            if(currentUser.getType().equals("Admin")){
                fbAddArticle.setVisibility(View.VISIBLE);
            }else{
                fbAddArticle.setVisibility(View.GONE);
            }
            rvSparePartsList = findViewById(R.id.rvSparePartsList);
            rvSparePartsList.setHasFixedSize(true);
            rvSparePartsList.setLayoutManager(new LinearLayoutManager(this));
            generalRepairAdapter = new GeneralRepairAdapter(arrayList);
            rvSparePartsList.setAdapter(generalRepairAdapter);
            rvSparePartsList.setItemViewCacheSize(10);
            arrayList.clear();
            generalRepairAdapter.notifyDataSetChanged();
            firebaseDatabaseHelper.queryGeneralArticles(this);

        }catch (Exception e)
        {
            Log.e(TAG, "onCreate: "+e.toString() );
        }
    }

    @Override
    public void onRetrieveFeedBackDataCompleted(ArrayList<GeneralRepairModel> models) {
        if (models.size() < 1) {
            tvNoData.setVisibility(View.VISIBLE);
            rvSparePartsList.setVisibility(View.INVISIBLE);
        } else {
            Collections.reverse(models);
            arrayList.addAll(models);
            generalRepairAdapter.notifyDataSetChanged();
            models.clear();
            tvNoData.setVisibility(View.GONE);
            rvSparePartsList.setVisibility(View.VISIBLE);
        }
    }
}