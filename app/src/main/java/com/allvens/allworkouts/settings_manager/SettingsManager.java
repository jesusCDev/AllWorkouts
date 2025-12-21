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
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.settings_manager.Notification_Manager.WorkoutNotificationManager;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosDragListener;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosTouchListener;

import java.util.Calendar;

import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.SwitchCompat;

public class SettingsManager {

    private Context context;
    private SettingsUIManager ui_manager;
    private SettingsPrefsManager settingsPrefs;
    private WorkoutNotificationManager notiManager;
    private SwitchCompat[] posSwitches = new SwitchCompat[5];
    private int switchPosTracker = 0;

    public SettingsManager(Context context){
        this.context  = context;
        ui_manager    = new SettingsUIManager(context);
        settingsPrefs = new SettingsPrefsManager(context);
        notiManager   = new WorkoutNotificationManager(context   , settingsPrefs.getPrefSetting(PreferencesValues.NOTIFICATION_ON),
                settingsPrefs.get_NotifiHour(), settingsPrefs.get_NotifiMinute());
    }

    /****************************************
     /**** SETUP METHODS
     ****************************************/

    public void set_SettingsValues(Switch sVibrate, Switch sSound, Switch sNotification) {
        sVibrate.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.VIBRATE_ON));
        sSound.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.SOUND_ON));
        sNotification.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.NOTIFICATION_ON));
    }
    
    public void set_SettingsValues(Switch sVibrate, Switch sSound, Switch sNotification, Switch sMediaControls) {
        set_SettingsValues(sVibrate, sSound, sNotification);
        sMediaControls.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.MEDIA_CONTROLS_ON));
    }
    
    public void set_SettingsValues(Switch sVibrate, Switch sSound, Switch sNotification, Switch sMediaControls, Switch sShowSongTitle) {
        set_SettingsValues(sVibrate, sSound, sNotification, sMediaControls);
        // Default to false for show song title (requires extra permission)
        sShowSongTitle.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.SHOW_SONG_TITLE, false));
    }
    
    /**
     * Setup display settings switches (default to true if not set)
     */
    public void set_DisplaySettingsValues(Switch sShowTimeEstimate, Switch sShowStatsCards) {
        // Default to true for display settings
        sShowTimeEstimate.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.SHOW_TIME_ESTIMATE, true));
        sShowStatsCards.setChecked(settingsPrefs.getPrefSetting(PreferencesValues.SHOW_STATS_CARDS, true));
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
        // Clear existing views and reset tracker for refresh
        llWorkoutsAndPositions.removeAllViews();
        switchPosTracker = 0;
        posSwitches = new SwitchCompat[5];
        
        WorkoutBasicsPrefsChecker prefsChecker = new WorkoutBasicsPrefsChecker(context);
        WorkoutPosTouchListener touchListener  = new WorkoutPosTouchListener(context);

        for(WorkoutPosAndStatus w : prefsChecker.getWorkoutPositions(true)) {
            llWorkoutsAndPositions.addView(
                    create_WorkoutPosContainer(w, touchListener, prefsChecker));
        }
        insure_OneWorkoutIsOn();
    }

    /* ---------- Helper to build each row ---------- */

    private ConstraintLayout create_WorkoutPosContainer(final WorkoutPosAndStatus workout,
                                                        WorkoutPosTouchListener touchListener,
                                                        final WorkoutBasicsPrefsChecker prefsChecker){

        ConstraintLayout container = new ConstraintLayout(context);
        container.setId(workout.getResourceID());
        container.setOnTouchListener(touchListener);
        container.setOnDragListener(new WorkoutPosDragListener(context, touchListener, prefsChecker));
        
        // Add modern styling with padding and background
        int padding = new Helper(context).get_dpFromPixels(16);
        int paddingVertical = new Helper(context).get_dpFromPixels(12);
        container.setPadding(padding, paddingVertical, padding, paddingVertical);
        
        // Add subtle background for each item
        container.setBackgroundColor(ContextCompat.getColor(context, R.color.background_elevated));
        
        // Add margin between items
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, new Helper(context).get_dpFromPixels(8));
        container.setLayoutParams(layoutParams);

        /* Drag handle with better styling */
        ImageView ivDrag = new ImageView(context);
        ivDrag.setId(R.id.btn_Pos_id);
        ivDrag.setImageResource(R.drawable.ic_drag_handle_black_24dp);
        ivDrag.setPadding(0, 0, new Helper(context).get_dpFromPixels(12), 0);
        // Tint icon for dark theme with proper opacity
        DrawableCompat.setTint(ivDrag.getDrawable(),
                ContextCompat.getColor(context, R.color.unSelectedButton));
        ivDrag.setAlpha(0.6f);

        /* Title with modern typography */
        TextView tvTitle = new TextView(context);
        tvTitle.setId(R.id.tv_Pos_Id);
        tvTitle.setText(workout.getName());
        tvTitle.setTextColor(ContextCompat.getColor(context, R.color.selectedButton));
        tvTitle.setTextSize(16); // Use body text size
        tvTitle.setTypeface(tvTitle.getTypeface(), android.graphics.Typeface.NORMAL);

        /* Toggle switch with better spacing */
        SwitchCompat swToggle = new SwitchCompat(context);
        swToggle.setId(R.id.s_Pos_Id);
        swToggle.setChecked(workout.get_TurnOnStatus());
        swToggle.setOnCheckedChangeListener((CompoundButton buttonView, boolean isChecked) -> {
            prefsChecker.updateWorkoutStatusPreference(workout.getStatPrefKey(), isChecked);
            insure_OneWorkoutIsOn();
        });

        /* Keep reference for the "at least one on" rule */
        posSwitches[switchPosTracker++] = swToggle;

        /* Add & constrain with better spacing */
        container.addView(ivDrag);
        container.addView(tvTitle);
        container.addView(swToggle);

        ConstraintSet set = new ConstraintSet();
        set.clone(container);

        // Position drag handle with proper margins
        set.connect(ivDrag.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID, ConstraintSet.START);
        set.connect(ivDrag.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(ivDrag.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        
        // Position title with proper spacing from drag handle
        set.connect(tvTitle.getId(), ConstraintSet.START, ivDrag.getId(), ConstraintSet.END);
        set.connect(tvTitle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(tvTitle.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);
        set.constrainWidth(tvTitle.getId(), 0); // Use 0dp width with constraints
        set.connect(tvTitle.getId(), ConstraintSet.END, swToggle.getId(), ConstraintSet.START);
        set.setMargin(tvTitle.getId(), ConstraintSet.START, new Helper(context).get_dpFromPixels(4));
        set.setMargin(tvTitle.getId(), ConstraintSet.END, new Helper(context).get_dpFromPixels(16));

        // Position toggle switch
        set.connect(swToggle.getId(), ConstraintSet.END, ConstraintSet.PARENT_ID, ConstraintSet.END);
        set.connect(swToggle.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP);
        set.connect(swToggle.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM);

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
        // Refresh all day button states to show current configuration
        ui_manager.updateDailyNotificationColors(settingsPrefs.get_NotificationDayValue(0), settingsPrefs.get_NotificationDayValue(1),
                settingsPrefs.get_NotificationDayValue(2), settingsPrefs.get_NotificationDayValue(3), settingsPrefs.get_NotificationDayValue(4),
                settingsPrefs.get_NotificationDayValue(5), settingsPrefs.get_NotificationDayValue(6));
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

        mTimePicker = new TimePickerDialog(context, R.style.DarkTimePickerDialog, new TimePickerDialog.OnTimeSetListener() {
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
