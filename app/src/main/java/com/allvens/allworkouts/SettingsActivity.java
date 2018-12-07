package com.allvens.allworkouts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.settings_manager.SettingsScene_Manager;

public class SettingsActivity extends AppCompatActivity{

    private SettingsScene_Manager settingsScene_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        LinearLayout ll_settings_WorkoutPositions = findViewById(R.id.ll_settings_WorkoutPositions);

        TextView tvTimeDisplay = findViewById(R.id.tv_settings_Time);

        Switch sVibrate = findViewById(R.id.s_settings_Vibrate);
        Switch sSound = findViewById(R.id.s_settings_Sound);
        Switch sScreenOn = findViewById(R.id.s_settings_ScreenTurnOn);
        Switch sNotification = findViewById(R.id.s_settings_Notification);

        Button btnSu = findViewById(R.id.btn_settings_notificationDaySU);
        Button btnM = findViewById(R.id.btn_settings_notificationDayM);
        Button btnTu = findViewById(R.id.btn_settings_notificationDayTU);
        Button btnW = findViewById(R.id.btn_settings_notificationDayW);
        Button btnTh = findViewById(R.id.btn_settings_notificationDayTH);
        Button btnF = findViewById(R.id.btn_settings_notificationDayF);
        Button btnSa = findViewById(R.id.btn_settings_notificationDaySA);


        settingsScene_manager = new SettingsScene_Manager(this);
        settingsScene_manager.setUp_SettingsValues(sVibrate, sSound, sScreenOn, sNotification);
        settingsScene_manager.setUp_WorkoutsAndPositions(ll_settings_WorkoutPositions);
        settingsScene_manager.setUp_TimeDisplay(tvTimeDisplay);
        settingsScene_manager.setUP_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);

        sVibrate.setOnCheckedChangeListener(settingsScene_manager.update_PrefSettings(Preferences_Values.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settingsScene_manager.update_PrefSettings(Preferences_Values.SOUND_ON));
        sScreenOn.setOnCheckedChangeListener(settingsScene_manager.update_PrefSettings(Preferences_Values.SCREEN_ON));

        sNotification.setOnCheckedChangeListener(settingsScene_manager.update_NotfiSettings(Preferences_Values.NOTIFICATION_ON));
    }

    public void btnAction_ResetToDefaults(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        Workout_Wrapper wrapper = new Workout_Wrapper(SettingsActivity.this);
                        wrapper.open();
                        wrapper.delete_AllWorkouts();
                        wrapper.delete_AllHistoryWorkouts();
                        wrapper.close();
                        new Toast(SettingsActivity.this).makeText(SettingsActivity.this, "Workout Data Deleted", Toast.LENGTH_SHORT).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        new Toast(SettingsActivity.this).makeText(SettingsActivity.this, "Nothing was deleted", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("All Workouts Will Be Deleted?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void btnAction_SetNotificationTime(View view){
        settingsScene_manager.update_NotificationTime(view);
    }

    public void btnAction_setDayNotifications(View view){
        settingsScene_manager.update_DayOfNotification(((Button)view));
    }
}
