package com.allvens.allworkouts.settings_manager;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Helper;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.Notification_Manager.Notification_Manager;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPos_DragListener;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPos_TouchListener;

import java.util.Calendar;

import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;

public class Settings_Manager {

    private Context context;
    private Settings_UI_Manager ui_manager;
    private SettingsPrefs_Manager settingsPrefs;
    private Notification_Manager notiManager;
    private SwitchCompat[] posSwitches = new SwitchCompat[4];
    private int switchPosTracker = 0;

    public Settings_Manager(Context context){
        this.context  = context;
        ui_manager    = new Settings_UI_Manager(context);
        settingsPrefs = new SettingsPrefs_Manager(context);
        notiManager   = new Notification_Manager(context   , settingsPrefs.get_PrefSetting(Preferences_Values.NOTIFICATION_ON),
                settingsPrefs.get_NotifiHour(), settingsPrefs.get_NotifiMinute());
    }

    /****************************************
     /**** SETUP METHODS
     ****************************************/

    public void set_SettingsValues(Switch sVibrate, Switch sSound, Switch sNotification) {
        sVibrate.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.VIBRATE_ON));
        sSound.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.SOUND_ON));
        sNotification.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.NOTIFICATION_ON));
    }

    /********** Notification Settings **********/

    public void setUp_TimeDisplay(TextView tvTime) {
        ui_manager.updateTimeStamp(tvTime, notiManager.get_Hour(), notiManager.get_Min());
        notiManager.set_Time(notiManager.get_Hour(), notiManager.get_Min());
    }

    public void setUP_DailyNotificationBtns(Button btnSu, Button btnM, Button btnTu, Button btnW, Button btnTh, Button btnF, Button btnSa) {
        ui_manager.set_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);
        ui_manager.updateDailyNotificationColors(settingsPrefs.get_NotificationDayValue(0), settingsPrefs.get_NotificationDayValue(1),
                settingsPrefs.get_NotificationDayValue(2), settingsPrefs.get_NotificationDayValue(3), settingsPrefs.get_NotificationDayValue(4),
                settingsPrefs.get_NotificationDayValue(5), settingsPrefs.get_NotificationDayValue(6));
    }


    /* ---------- Workout Position & Status ---------- */

    public void setUp_WorkoutsAndPositions(LinearLayout llWorkoutsAndPositions) {
        WorkoutBasicsPrefs_Checker prefsChecker = new WorkoutBasicsPrefs_Checker(context);
        WorkoutPos_TouchListener touchListener  = new WorkoutPos_TouchListener(context);

        for(WorkoutPosAndStatus w : prefsChecker.get_WorkoutsPos(true)) {
            llWorkoutsAndPositions.addView(
                    create_WorkoutPosContainer(w, touchListener, prefsChecker));
        }
        insure_OneWorkoutIsOn();
    }

    /* ---------- Helper to build each row ---------- */

    private ConstraintLayout create_WorkoutPosContainer(final WorkoutPosAndStatus workout,
                                                        WorkoutPos_TouchListener touchListener,
                                                        final WorkoutBasicsPrefs_Checker prefsChecker){

        ConstraintLayout container = new ConstraintLayout(context);
        container.setId(workout.getResourceID());
        container.setOnTouchListener(touchListener);
        container.setOnDragListener(new WorkoutPos_DragListener(context, touchListener, prefsChecker));

        /* Drag handle */
        ImageView ivDrag = new ImageView(context);
        ivDrag.setId(R.id.btn_Pos_id);
        ivDrag.setImageResource(R.drawable.ic_drag_handle_black_24dp);
        ivDrag.setPadding(0, 0, new Helper(context).get_dpFromPixels(8), 0);
        // Tint icon white for dark theme
        DrawableCompat.setTint(ivDrag.getDrawable(),
                ContextCompat.getColor(context, R.color.selectedButton));

        /* Title */
        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workout.getName());
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.selectedButton));

        /* Toggle switch */
        SwitchCompat swToggle = new SwitchCompat(context);
        swToggle.setId(R.id.s_Pos_Id);
        swToggle.setChecked(workout.get_TurnOnStatus());
        swToggle.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            prefsChecker.update_WorkoutStatusPref(workout.getStatPrefKey(), isChecked);
            insure_OneWorkoutIsOn();
        });

        /* Keep reference for the “at least one on” rule */
        posSwitches[switchPosTracker++] = swToggle;

        /* Add & constrain */
        container.addView(ivDrag);
        container.addView(tvTitle);
        container.addView(swToggle);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(ivDrag.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(tvTitle.getId(), ConstraintSet.START, ivDrag.getId(), ConstraintSet.END);
        set.centerVertically(ivDrag.getId(), tvTitle.getId());

        set.connect(swToggle.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.centerVertically(tvTitle.getId(), swToggle.getId());

        set.applyTo(container);

        return container;
    }

    private void insure_OneWorkoutIsOn(){
        int on = 0;
        
        for(SwitchCompat s : posSwitches){
            if(s != null && s.isChecked()) on++;
        }

        for(SwitchCompat s : posSwitches){
            if(s == null) continue;
            s.setClickable(!(on == 1 && s.isChecked()));
        }
    }

    public void update_DayOfNotification(Button btn) {
        int dayChanged;

        switch (btn.getId()){
            case R.id.btn_settings_notificationDaySU:
                dayChanged = 0;
                break;
            case R.id.btn_settings_notificationDayM:
                dayChanged = 1;
                break;
            case R.id.btn_settings_notificationDayTU:
                dayChanged = 2;
                break;
            case R.id.btn_settings_notificationDayW:
                dayChanged = 3;
                break;
            case R.id.btn_settings_notificationDayTH:
                dayChanged = 4;
                break;
            case R.id.btn_settings_notificationDayF:
                dayChanged = 5;
                break;
            default:
                dayChanged = 6;
                break;
        }

        settingsPrefs.update_NotificationDay(dayChanged);
        ui_manager.updateDailyNotificationBtnStyle(btn, settingsPrefs.get_NotificationDayValue(dayChanged));
    }

    public CompoundButton.OnCheckedChangeListener update_PrefSettings(final String prefKey){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.update_PrefSetting(prefKey, isChecked);
            }
        };
    }

    public CompoundButton.OnCheckedChangeListener update_NotfiSettings(final String prefKey){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.update_PrefSetting(prefKey, isChecked);
                notiManager.set_NotificationOn(isChecked);

                if(isChecked){
                    notiManager.create_Notification();
                }
                else{
                    notiManager.cancel_Notification();
                }
            }
        };
    }

    public void update_NotificationTime(final View view) {
        Calendar currentTime = Calendar.getInstance();
        int hour             = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute           = currentTime.get(Calendar.MINUTE);

        TimePickerDialog mTimePicker;

        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                ui_manager.updateTimeStamp(((TextView)view), selectedHour, selectedMinute);
                settingsPrefs.update_NotificationTime(selectedHour, selectedMinute);
                notiManager.update_Time(selectedHour, selectedMinute);
            }
        }, hour, minute, false);

        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }
}
