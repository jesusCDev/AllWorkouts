package com.allvens.allworkouts.settings_manager;

import android.widget.TextView;

public class SettingsScene_UI_Manager {

    public void update_TimeStamp(TextView tv, int selectedHour, int selectedMin){
        StringBuilder timeStamp = new StringBuilder();

        timeStamp.append(fix_Hour(selectedHour));
        timeStamp.append(":");
        timeStamp.append(fix_Min(selectedMin));
        timeStamp.append(fix_amPm(selectedHour));
        tv.setText(timeStamp.toString());
    }

    private String fix_amPm(int selectedHour) {
        if(selectedHour < 12){
            return "am";
        }else{
            return "pm";
        }
    }

    private String fix_Hour(int selectedHour) {
        if(selectedHour == 0) return "12";
        if(selectedHour <= 12){
            return Integer.toString(selectedHour);
        }else{
            return Integer.toString(selectedHour - 12);
        }
    }

    private String fix_Min(int selectedMin) {
        if(selectedMin < 10){
            return ("0" + Integer.toString(selectedMin));
        }else{
            return Integer.toString(selectedMin);
        }
    }
}
