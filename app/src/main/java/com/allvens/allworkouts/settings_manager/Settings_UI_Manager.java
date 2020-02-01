package com.allvens.allworkouts.settings_manager;

import android.content.Context;
import android.os.Build;
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

    public void update_TimeStamp(TextView tv, int selectedHour, int selectedMin){
        StringBuilder timeStamp = new StringBuilder();

        timeStamp.append(fix_Hour(selectedHour));
        timeStamp.append(":");
        timeStamp.append(fix_Min(selectedMin));
        timeStamp.append(" " + fix_amPm(selectedHour));
        tv.setText(timeStamp.toString());
    }

    /********** 24 Hour Converter to 12 Hour Format **********/

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

    /****************************************
     /**** NOTIFICATION SETTINGS
     ****************************************/

    public void set_DailyNotificationBtns(Button btnSu, Button btnM, Button btnTu, Button btnW,
                                          Button btnTh, Button btnF, Button btnSa){
        this.btnSu = btnSu;
        this.btnM = btnM;
        this.btnTu = btnTu;
        this.btnW = btnW;
        this.btnTh = btnTh;
        this.btnF = btnF;
        this.btnSa = btnSa;
    }

    public void update_DailyNotificationColors(boolean sun, boolean mon, boolean tue, boolean wed,
                                               boolean thur, boolean fri, boolean sat){
        update_DailyNotificationBtnStyle(btnSu, sun);
        update_DailyNotificationBtnStyle(btnM, mon);
        update_DailyNotificationBtnStyle(btnTu, tue);
        update_DailyNotificationBtnStyle(btnW, wed);
        update_DailyNotificationBtnStyle(btnTh, thur);
        update_DailyNotificationBtnStyle(btnF, fri);
        update_DailyNotificationBtnStyle(btnSa, sat);
    }

    public void update_DailyNotificationBtnStyle(Button btn, boolean notiOnDay) {
        if(notiOnDay){
            set_BtnStyle(btn, R.style.btn_settings_DaySelected);
        }else{
            set_BtnStyle(btn, R.style.btn_settings_DayUnSelected);
        }
    }

    private void set_BtnStyle(Button btn, int style){
        if (Build.VERSION.SDK_INT < 23) {
            btn.setTextAppearance(context, style);
        } else {
            btn.setTextAppearance(style);
        }
    }
}
