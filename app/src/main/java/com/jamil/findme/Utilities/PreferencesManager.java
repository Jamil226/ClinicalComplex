package com.jamil.findme.Utilities;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.jamil.findme.Models.Visitor;
import com.jamil.findme.Models.WorkShopModel;
import com.jamil.findme.Models.User;


public class PreferencesManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public PreferencesManager(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public void saveCurrentUser(User user) {
        editor.putString("currentUser", new Gson().toJson(user instanceof Visitor ? (Visitor) user : (WorkShopModel) user)).commit();
        editor.putBoolean("isStudent", user instanceof Visitor).commit();
    }

    public User getCurrentUser() {
        if (sharedPreferences.getBoolean("isStudent", true))
            return new Gson().fromJson(sharedPreferences.getString("currentUser", ""), Visitor.class);
        else
            return new Gson().fromJson(sharedPreferences.getString("currentUser", ""), WorkShopModel.class);

    }
}
