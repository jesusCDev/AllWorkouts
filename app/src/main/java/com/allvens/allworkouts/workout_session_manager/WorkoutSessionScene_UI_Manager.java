package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.settings_manager.SettingsPrefs_Manager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;

public class WorkoutSessionScene_UI_Manager {

    private Workout workout;
    private int progress = 0;
    private SettingsPrefs_Manager prefs_manager;
    private Vibrator vibrator;
    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    private Context context;
    private TextView tv_WorkoutName;
    private LinearLayout llTimeImageHolder;

    private Button btn_ChangeScreens;
    private TextView tv_Timer;

    private TextView[] aTvBottomValues = new TextView[5];

    public WorkoutSessionScene_UI_Manager(Context context, TextView tv_workout_workoutName,
                                          LinearLayout llTimeImageHolder, TextView tvValue1, TextView tvValue2,
                                          TextView tvValue3, TextView tvValue4, TextView tvValue5, Button btn_workout_completeTask) {
        this.context = context;
        tv_WorkoutName = tv_workout_workoutName;
        this.llTimeImageHolder = llTimeImageHolder;

        aTvBottomValues[0] = tvValue1;
        aTvBottomValues[1] = tvValue2;
        aTvBottomValues[2] = tvValue3;
        aTvBottomValues[3] = tvValue4;
        aTvBottomValues[4] = tvValue5;

        btn_ChangeScreens = btn_workout_completeTask;
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        prefs_manager = new SettingsPrefs_Manager(context);
    }

    public void set_Progress(int progress){
        this.progress = progress;
    }

    public int get_Progress() {
        return progress;
    }

    public void set_Workout(Workout workout) {
        this.workout = workout;
    }

    public void set_TimeTV(long seconds) {
        tv_Timer.setText(Long.toString(seconds));
    }

    ////////////////////////////////////////////////////// Screen changing methods

    private void update_BottomNextValue(){
        aTvBottomValues[progress].setTextColor(Color.BLACK);
    }

    private void update_LastValue(){
        aTvBottomValues[(progress - 1)].setTextColor(context.getResources().getColor(R.color.colorPrimaryDark));
    }

    public void update_BottomValues(){
        aTvBottomValues[0].setText(Integer.toString(workout.get_WorkoutValue(0)));
        aTvBottomValues[1].setText(Integer.toString(workout.get_WorkoutValue(1)));
        aTvBottomValues[2].setText(Integer.toString(workout.get_WorkoutValue(2)));
        aTvBottomValues[3].setText(Integer.toString(workout.get_WorkoutValue(3)));
        aTvBottomValues[4].setText(Integer.toString(workout.get_WorkoutValue(4)));
    }

    public void changeScreen_Workout() {
        tv_WorkoutName.setText(workout.get_WorkoutName(progress));
        btn_ChangeScreens.setText("Complete");

        update_BottomNextValue();

        clear_MainView();
        ImageView iv_Workout = new ImageView(context);
    }

    public void changeScreen_Timer() {
        tv_WorkoutName.setText(workout.get_WorkoutName(progress));
        btn_ChangeScreens.setText("Next");

        clear_MainView();

        tv_Timer = new TextView(context);

        update_LastValue();
        update_BottomNextValue();

        // todo set text
//        tv_Timer.setText(context.getResources().getString(R.string.home_Finished));
        tv_Timer.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // todo set style
//        setStyle(tv_finished, R.style.TS_StartingCountDown_Ending);

        llTimeImageHolder.addView(tv_Timer);
    }

    ////////////////////////////////////////////////////// random methods

    private void clear_MainView(){
        llTimeImageHolder.removeAllViews();
    }


    public void play_StartEndSound(){
        if(prefs_manager.get_PrefSetting(Preferences_Values.SOUND_ON)){
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        }
    }

    public void vibrate(){
        if(prefs_manager.get_PrefSetting(Preferences_Values.VIBRATE_ON)){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                vibrator.vibrate(500);
            }
        }
    }

}
