package com.allvens.allworkouts.settings_manager;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.R;

public class Settings_UI_Manager {

    private Button btnSu;
    private Button btnM;
    private Button btnTu;
    private Button btnW;
    private Button btnTh;
    private Button btnF;
    private Button btnSa;
    private Context context;

    public Settings_UI_Manager(Context context){
        this.context = context;
    }

    /****************************************
     /**** TIME DISPLAY
     ****************************************/

    public void updateTimeStamp(TextView tv, int selectedHour, int selectedMin){
        StringBuilder timeStamp = new StringBuilder();

        timeStamp.append(fixHour(selectedHour));
        timeStamp.append(":");
        timeStamp.append(fixMin(selectedMin));
        timeStamp.append(" " + fixAmPm(selectedHour));
        tv.setText(timeStamp.toString());
    }

    private String fixAmPm(int selectedHour) {
        if(selectedHour < 12){
            return "am";
        }

        return "pm";
    }

    private String fixHour(int selectedHour) {
        if(selectedHour == 0) return "12";
        if(selectedHour <= 12){
            return Integer.toString(selectedHour);
        }else{
            return Integer.toString(selectedHour - 12);
        }
    }

    private String fixMin(int selectedMin) {
        if(selectedMin < 10){
            return ("0" + Integer.toString(selectedMin));
        }

        return Integer.toString(selectedMin);
    }

    /****************************************
     /**** NOTIFICATION SETTINGS
     ****************************************/

    public void set_DailyNotificationBtns(Button btnSu, Button btnM, Button btnTu, Button btnW,
                                          Button btnTh, Button btnF, Button btnSa){
        this.btnSu = btnSu;
        this.btnM  = btnM;
        this.btnTu = btnTu;
        this.btnW  = btnW;
        this.btnTh = btnTh;
        this.btnF  = btnF;
        this.btnSa = btnSa;
    }

    public void updateDailyNotificationColors(boolean sun, boolean mon, boolean tue, boolean wed,
                                              boolean thur, boolean fri, boolean sat){
        updateDailyNotificationBtnStyle(btnSu, sun);
        updateDailyNotificationBtnStyle(btnM, mon);
        updateDailyNotificationBtnStyle(btnTu, tue);
        updateDailyNotificationBtnStyle(btnW, wed);
        updateDailyNotificationBtnStyle(btnTh, thur);
        updateDailyNotificationBtnStyle(btnF, fri);
        updateDailyNotificationBtnStyle(btnSa, sat);
    }

    public void updateDailyNotificationBtnStyle(Button btn, boolean notiOnDay) {
        if(notiOnDay){
            // Selected state: white text on accent background
            btn.setTextColor(ContextCompat.getColor(context, R.color.on_primary));
            btn.setBackgroundColor(ContextCompat.getColor(context, R.color.colorAccent));
            btn.setTypeface(btn.getTypeface(), android.graphics.Typeface.BOLD);
        }
        else {
            // Unselected state: white text on transparent background
            btn.setTextColor(ContextCompat.getColor(context, R.color.on_primary));
            btn.setBackgroundColor(ContextCompat.getColor(context, android.R.color.transparent));
            btn.setTypeface(btn.getTypeface(), android.graphics.Typeface.NORMAL);
        }
    }
}
