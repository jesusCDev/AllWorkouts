package com.allvens.allworkouts.settings_manager.WorkoutPos;

import android.app.TimePickerDialog;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.allvens.allworkouts.DebuggingMethods;
import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.SettingsPrefs_Checker;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefs_Checker;
import com.allvens.allworkouts.settings_manager.Settings_UI_Manager;

import java.util.Calendar;

public class WorkoutPos_Manager {

    private Context context;
    private Settings_UI_Manager ui_manager;
    private SettingsPrefs_Checker settingsPrefs;
    private Switch[] posSwitches = new Switch[4];
    private int switchPosTracker = 0;

    public WorkoutPos_Manager(Context context){
        this.context = context;
        ui_manager = new Settings_UI_Manager();
        settingsPrefs = new SettingsPrefs_Checker(context);
    }

    public void setUp_WorkoutsAndPositions(LinearLayout llWorkoutsAndPositions) {
        WorkoutBasicsPrefs_Checker workout_basicsPrefs = new WorkoutBasicsPrefs_Checker(context);
        WorkoutPos_TouchListener touchListener = new WorkoutPos_TouchListener(context);

        for(WorkoutPosAndStatus allWorkoutsAndPositions: workout_basicsPrefs.get_WorkoutsPos(true)) {
            llWorkoutsAndPositions.addView(create_WorkoutPosContainer(allWorkoutsAndPositions, touchListener, workout_basicsPrefs ));
        }
        check_AtleastOneSwitchOn();
    }

    public void setUp_SettingsValues(Switch sVibrate, Switch sSound, Switch sScreenOn, Switch sNotification) {

        DebuggingMethods.pop("Working");
        sVibrate.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.VIBRATE_ON));
        sSound.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.SOUND_ON));
        sScreenOn.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.SCREEN_ON));
        sNotification.setChecked(settingsPrefs.get_PrefSetting(Preferences_Values.NOTIFICATION_ON));
        DebuggingMethods.pop("Break");
    }

    private ConstraintLayout create_WorkoutPosContainer(final WorkoutPosAndStatus workout, WorkoutPos_TouchListener touchListener, final WorkoutBasicsPrefs_Checker workout_basicsPrefs){

        ConstraintLayout container = new ConstraintLayout(context);
        container.setId(workout.getResourceID());

        container.setOnTouchListener(touchListener);
        container.setOnDragListener(new WorkoutPos_DragListener(touchListener, workout_basicsPrefs));

        ImageView ivDragHandle = new ImageView(context);
        ivDragHandle.setImageResource(R.drawable.ic_drag_handle_black_24dp);
        ivDragHandle.setId(R.id.btn_Pos_id);

        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workout.getName());

        final Switch sTurnOffOn = new Switch(context);
        sTurnOffOn.setId(R.id.s_Pos_Id);
        sTurnOffOn.setChecked(workout.get_TurnOnStatus());
        sTurnOffOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                workout_basicsPrefs.update_WorkoutStatusPref(workout.getStatPrefKey(), isChecked);
                check_AtleastOneSwitchOn();
            }
        });
        posSwitches[switchPosTracker] = sTurnOffOn;
        switchPosTracker++;

        container.addView(ivDragHandle);
        container.addView(tvTitle);
        container.addView(sTurnOffOn);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        set.connect(ivDragHandle.getId(), ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT);
        set.connect(tvTitle.getId(), ConstraintSet.START, ivDragHandle.getId(), ConstraintSet.END);
        set.connect(sTurnOffOn.getId(), ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT);

        set.applyTo(container);

        return container;
    }

    private void check_AtleastOneSwitchOn(){
        int onSwitches = 0;
        for(Switch switchValue: posSwitches){
            if(switchValue.isChecked()){
                onSwitches++;
            }
        }

        DebuggingMethods.pop("Switches On: " + onSwitches);

        if(onSwitches == 1){
            for(Switch switchValue: posSwitches){
                if(switchValue.isChecked()){
                    switchValue.setClickable(false);
                }
            }
        }else{
            for(Switch switchValue: posSwitches){
                if(!switchValue.isClickable()){
                    switchValue.setClickable(true);
                }
            }
        }
    }

    public void update_NotificationTime(final View view) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);


        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                ui_manager.update_TimeStamp(((TextView)view), selectedHour, selectedMinute);
                set_TimeForNotification(selectedHour, selectedMinute);
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void set_TimeForNotification(int hour, int minute){
        settingsPrefs.update_NotificationTime(hour, minute);
    }

    public void update_DayOfNotification(String day) {
        switch (day.toLowerCase()){
            case "su":
                settingsPrefs.update_NotificationDay(0);
                break;
            case "m":
                settingsPrefs.update_NotificationDay(1);
                break;
            case "tu":
                settingsPrefs.update_NotificationDay(2);
                break;
            case "w":
                settingsPrefs.update_NotificationDay(3);
                break;
            case "th":
                settingsPrefs.update_NotificationDay(4);
                break;
            case "f":
                settingsPrefs.update_NotificationDay(5);
                break;
            default:
                settingsPrefs.update_NotificationDay(6);
                break;
        }
    }

    public CompoundButton.OnCheckedChangeListener update_PrefSettings(final String prefKey){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                settingsPrefs.update_PrefSetting(prefKey, isChecked);
            }
        };
    }
}
