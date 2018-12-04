package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;

import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPos_Manager;

public class SettingsActivity extends AppCompatActivity{

    private WorkoutPos_Manager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        LinearLayout ll_settings_WorkoutPositions = findViewById(R.id.ll_settings_WorkoutPositions);

        Switch sVibrate = findViewById(R.id.s_settings_Vibrate);
        Switch sSound = findViewById(R.id.s_settings_Sound);
        Switch sScreenOn = findViewById(R.id.s_settings_ScreenTurnOn);
        Switch sNotification = findViewById(R.id.s_settings_Notification);


        manager = new WorkoutPos_Manager(this);
        manager.setUp_SettingsValues(sVibrate, sSound, sScreenOn, sNotification);
        manager.setUp_WorkoutsAndPositions(ll_settings_WorkoutPositions);

        sVibrate.setOnCheckedChangeListener(manager.update_PrefSettings(Preferences_Values.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(manager.update_PrefSettings(Preferences_Values.SOUND_ON));
        sScreenOn.setOnCheckedChangeListener(manager.update_PrefSettings(Preferences_Values.SCREEN_ON));
    }

    public void btnAction_ResetToDefaults(View view){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();
        wrapper.delete_AllWorkouts();
        wrapper.close();
    }

    public void btnAction_SetNotificationTime(View view){
        manager.update_NotificationTime(view);
    }

    public void btnAction_setDayNotifications(View view){
        manager.update_DayOfNotification(((Button)view).getText().toString());
    }
}
