package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefs_Checker {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    public SettingsPrefs_Checker(Context context){
        prefs = context.getSharedPreferences(Preferences_Values.PREFS_NAMES, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }


}
