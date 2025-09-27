package com.allvens.allworkouts;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.support.design.widget.Snackbar;
import android.view.View;

import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.Settings_Manager;

public class SettingsActivity extends AppCompatActivity{

    private Settings_Manager settings_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        LinearLayout ll_settings_WorkoutPositions = findViewById(R.id.ll_settings_WorkoutPositions);

        Switch sVibrate       = findViewById(R.id.s_settings_Vibrate);
        Switch sSound         = findViewById(R.id.s_settings_Sound);
        Switch sNotification  = findViewById(R.id.s_settings_Notification);
        Switch sMediaControls = findViewById(R.id.s_settings_MediaControls);

        TextView tvTimeDisplay = findViewById(R.id.tv_settings_Time);

        Button btnSu = findViewById(R.id.btn_settings_notificationDaySU);
        Button btnM  = findViewById(R.id.btn_settings_notificationDayM);
        Button btnTu = findViewById(R.id.btn_settings_notificationDayTU);
        Button btnW  = findViewById(R.id.btn_settings_notificationDayW);
        Button btnTh = findViewById(R.id.btn_settings_notificationDayTH);
        Button btnF  = findViewById(R.id.btn_settings_notificationDayF);
        Button btnSa = findViewById(R.id.btn_settings_notificationDaySA);

        settings_manager = new Settings_Manager(this);

        settings_manager.set_SettingsValues(sVibrate, sSound, sNotification, sMediaControls);
        settings_manager.setUp_WorkoutsAndPositions(ll_settings_WorkoutPositions);
        settings_manager.setUp_TimeDisplay(tvTimeDisplay);
        settings_manager.setUP_DailyNotificationBtns(btnSu, btnM, btnTu, btnW, btnTh, btnF, btnSa);

        sVibrate.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.VIBRATE_ON));
        sSound.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.SOUND_ON));
        sMediaControls.setOnCheckedChangeListener(settings_manager.update_PrefSettings(Preferences_Values.MEDIA_CONTROLS_ON));

        sNotification.setOnCheckedChangeListener(settings_manager.update_NotfiSettings(Preferences_Values.NOTIFICATION_ON));
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/
    public void btnAction_ShowDocumentation(View view){
        startActivity(new Intent(this, Settings_AppInfo_SelectorActivity.class));
    }

    /********** Settings Switch Value Changes **********/
    public void btnAction_ResetToDefaults(View view){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        WorkoutWrapper wrapper = new WorkoutWrapper(SettingsActivity.this);
                        wrapper.open();
                        wrapper.deleteAllWorkouts();
                        wrapper.deleteAllHistoryWorkouts();
                        wrapper.close();
                        showDarkToast("Workout Data Deleted");
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        showDarkToast("Nothing was deleted");
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DarkAlertDialog);

        builder.setMessage("All Workouts Will Be Deleted?")
                .setTitle("Reset to Defaults")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    public void btnAction_SetNotificationTime(View view){
        settings_manager.update_NotificationTime(view);
    }

    public void btnAction_setDayNotifications(View view){
        settings_manager.update_DayOfNotification(((Button)view));
    }
    
    /**
     * Show a toast message with proper dark theme styling
     */
    private void showDarkToast(String message) {
        // Try to use Snackbar for better theming support
        View rootView = findViewById(android.R.id.content);
        if (rootView != null) {
            Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
            // Style the snackbar with dark colors
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent));
            View snackbarView = snackbar.getView();
            snackbarView.setBackgroundColor(getResources().getColor(R.color.background_elevated));
            snackbar.show();
        } else {
            // Fallback to regular toast if snackbar fails
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}
