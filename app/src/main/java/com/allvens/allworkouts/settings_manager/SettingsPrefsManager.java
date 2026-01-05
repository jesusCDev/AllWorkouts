package com.allvens.allworkouts.settings_manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.allvens.allworkouts.data_manager.PreferencesValues;

public class SettingsPrefsManager {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private String[] notification_days;

    public SettingsPrefsManager(Context context){
        prefs = context.getSharedPreferences(PreferencesValues.PREFS_NAMES, Context.MODE_PRIVATE);
        edit  = prefs.edit();

        notification_days = prefs.getString(PreferencesValues.NOTIFICATION_DAYS, "-1,-1,-1,-1,-1,-1,-1").split(",");
    }

    public boolean getPrefSetting(String prefKey){
        return prefs.getBoolean(prefKey, false);
    }
    
    /**
     * Get boolean preference with custom default value
     */
    public boolean getPrefSetting(String prefKey, boolean defaultValue){
        return prefs.getBoolean(prefKey, defaultValue);
    }
    
    /**
     * Get string preference (returns null if not set)
     */
    public String getPrefSettingString(String prefKey){
        return prefs.getString(prefKey, null);
    }
    
    /**
     * Update string preference
     */
    public void update_PrefSetting(String prefKey, String value){
        edit.putString(prefKey, value);
        edit.commit();
    }
    
    /**
     * Get int preference
     */
    public int getPrefSettingInt(String prefKey, int defaultValue){
        return prefs.getInt(prefKey, defaultValue);
    }
    
    /**
     * Update int preference
     */
    public void update_PrefSetting(String prefKey, int value){
        edit.putInt(prefKey, value);
        edit.commit();
    }

    public int get_NotifiHour() {
        return prefs.getInt(PreferencesValues.NOTIFICATION_TIME_HOUR, 0);
    }

    public int get_NotifiMinute(){
        return prefs.getInt(PreferencesValues.NOTIFICATION_TIME_MINUTE, 0);
    }

    public boolean get_NotificationDayValue(int i) {
        // Refresh the array to ensure we have the latest values
        notification_days = prefs.getString(PreferencesValues.NOTIFICATION_DAYS, "-1,-1,-1,-1,-1,-1,-1").split(",");
        return (Integer.parseInt(notification_days[i]) == 1);
    }

    public void update_PrefSetting(String prefKey, boolean value){
        edit.putBoolean(prefKey, value);
        edit.commit();
    }

    public void update_NotificationTime(int hour, int minute) {
        edit.putInt(PreferencesValues.NOTIFICATION_TIME_HOUR, hour);
        edit.putInt(PreferencesValues.NOTIFICATION_TIME_MINUTE, minute);
        edit.commit();
    }

    public void update_NotificationDay(int i) {
        notification_days[i] = Integer.toString(Integer.parseInt(notification_days[i]) * -1);

        edit.putString(PreferencesValues.NOTIFICATION_DAYS, convert_ArrayToString(notification_days));
        edit.commit();
    }

    /**
     * Set a notification day to a specific enabled/disabled state (used for backup restore)
     * @param i Day index (0-6, Sunday to Saturday)
     * @param enabled true to enable notifications for this day, false to disable
     */
    public void set_NotificationDay(int i, boolean enabled) {
        // Refresh the array first to ensure we have the latest values
        notification_days = prefs.getString(PreferencesValues.NOTIFICATION_DAYS, "-1,-1,-1,-1,-1,-1,-1").split(",");

        // Set the value directly: 1 for enabled, -1 for disabled
        notification_days[i] = enabled ? "1" : "-1";

        edit.putString(PreferencesValues.NOTIFICATION_DAYS, convert_ArrayToString(notification_days));
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
