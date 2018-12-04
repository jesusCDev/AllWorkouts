package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

public class SettingsPrefs_Checker {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;

    private String[] notification_days;

    public SettingsPrefs_Checker(Context context){
        prefs = context.getSharedPreferences(Preferences_Values.PREFS_NAMES, Context.MODE_PRIVATE);
        edit = prefs.edit();

        notification_days = prefs.getString(Preferences_Values.NOTIFICATION_DAYS, "1,1,1,1,1,1,1").split(",");
    }

    public boolean get_PrefSetting(String prefKey){
        return prefs.getBoolean(prefKey, false);
    }

    public void update_PrefSetting(String prefKey, boolean value){
        edit.putBoolean(prefKey, value);
        edit.commit();
    }

    public void update_NotificationTime(int hour, int minute) {
        edit.putInt(Preferences_Values.NOTIFICATION_TIME_HOUR, hour);
        edit.putInt(Preferences_Values.NOTIFICATION_TIME_MINTUE, minute);
        edit.commit();
    }

    public void update_NotificationDay(int i) {
        notification_days[i] = Integer.toString(Integer.parseInt(notification_days[i]) * -1);
        edit.putString(Preferences_Values.NOTIFICATION_DAYS, convert_ArrayToString(notification_days));
        edit.commit();
    }

    private String convert_ArrayToString(String[] notification_days){
        StringBuilder sb = new StringBuilder();

        for(String day: notification_days){
            sb.append(day);
            sb.append(",");
        }
        sb.deleteCharAt((sb.length() - 1));

        return sb.toString();
    }
}
