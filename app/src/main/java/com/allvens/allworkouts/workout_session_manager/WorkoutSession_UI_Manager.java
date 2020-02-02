package com.allvens.allworkouts.workout_session_manager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.settings_manager.SettingsPrefs_Manager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.iambedant.text.OutlineTextView;

public class WorkoutSession_UI_Manager {

    private Context context;
    private TextView tv_WorkoutName;
    private LinearLayout llWorkoutHelper;

    private Button btn_ChangeScreens;
    private ImageButton btn_WorkoutHelper;

    private TextView tvTimerHolder;
    private ImageView ivWorkoutImageHolder;

    private Workout workout;
    private TextView[] aTvWorkoutValues = new TextView[5];
    private TextView tvFront;
    private TextView tvBack;

    boolean soundOn;
    boolean vibrateOn;
    private Vibrator vibrator;
    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    private int progress = 0;

    public WorkoutSession_UI_Manager(Context context, Workout workout, TextView tv_workout_workoutName,
                                     ImageView ivWorkoutImageHolder, TextView tvTimerHolder, LinearLayout llWorkoutHelper,
                                     OutlineTextView tvFront, TextView tvBack, TextView tvValue1, TextView tvValue2,
                                     TextView tvValue3, TextView tvValue4, TextView tvValue5, Button btn_ChangeScreens, ImageButton btn_WorkoutHelper) {
        this.context = context;
        tv_WorkoutName = tv_workout_workoutName;
        this.llWorkoutHelper = llWorkoutHelper;

        this.ivWorkoutImageHolder = ivWorkoutImageHolder;
        this.tvTimerHolder = tvTimerHolder;

        this.tvFront = tvFront;
        this.tvBack = tvBack;

        aTvWorkoutValues[0] = tvValue1;
        aTvWorkoutValues[1] = tvValue2;
        aTvWorkoutValues[2] = tvValue3;
        aTvWorkoutValues[3] = tvValue4;
        aTvWorkoutValues[4] = tvValue5;

        this.btn_ChangeScreens = btn_ChangeScreens;
        this.btn_WorkoutHelper = btn_WorkoutHelper;

        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        SettingsPrefs_Manager prefs_manager = new SettingsPrefs_Manager(context);
        soundOn = prefs_manager.get_PrefSetting(Preferences_Values.SOUND_ON);
        vibrateOn = prefs_manager.get_PrefSetting(Preferences_Values.VIBRATE_ON);

        this.workout = workout;

        update_WorkoutValuesTextViews();
    }

    /****************************************
     /**** SETTER/GETTER METHODS
     ****************************************/

    public void set_Progress(int progress){
        this.progress = progress;
    }

    public int get_Progress() {
        return progress;
    }

    /****************************************
     /**** UI UPDATERS
     ****************************************/

    /********** Timer View  **********/

    public void set_TimeTV(long seconds) {
        tvTimerHolder.setText(Long.toString(seconds));
    }

    /********** Workout Values - Methods **********/

    private void update_WorkoutValuesTextViews(){
        aTvWorkoutValues[0].setText(Integer.toString(workout.get_WorkoutValue(0)));
        aTvWorkoutValues[1].setText(Integer.toString(workout.get_WorkoutValue(1)));
        aTvWorkoutValues[2].setText(Integer.toString(workout.get_WorkoutValue(2)));
        aTvWorkoutValues[3].setText(Integer.toString(workout.get_WorkoutValue(3)));
        aTvWorkoutValues[4].setText(Integer.toString(workout.get_WorkoutValue(4)));
    }

    private void update_WorkoutValuesNextValue(){
        setStyle_ForTextView(aTvWorkoutValues[progress], R.style.tv_WorkoutSession_CurrentWorkoutValue);
    }

    private void update_WorkoutValuesLastValue(){
        aTvWorkoutValues[(progress - 1)].setTextColor(context.getResources().getColor(R.color.unSelectedButton));
        setStyle_ForTextView(aTvWorkoutValues[(progress - 1)], R.style.tv_WorkoutSession_WorkoutValue);
    }

    /****************************************
     /**** SCREEN CHANGERS
     ****************************************/

    public void changeScreen_Workout() {
        Activity act = (Activity)context;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                tv_WorkoutName.setText(workout.get_WorkoutName(progress));
                btn_ChangeScreens.setText("Complete");


                tvFront.setText(Integer.toString(workout.get_WorkoutValue(progress)));
                tvBack.setText(Integer.toString(workout.get_WorkoutValue(progress)));

                update_WorkoutValuesNextValue();

                setVisibility_TextView(tvTimerHolder, false);

                setVisibility_TextView(tvFront, true);
                setVisibility_TextView(tvBack, true);

                setVisibility_ImageView(ivWorkoutImageHolder, true);
                ivWorkoutImageHolder.setImageResource(workout.get_WorkoutImage(progress));
            } });
    }


    public void changeScreen_Timer() {

        Activity act = (Activity)context;
        act.runOnUiThread(new Runnable(){
            @Override
            public void run() {
                tv_WorkoutName.setText(workout.get_WorkoutName(progress));
                btn_ChangeScreens.setText("Next");

                setVisibility_ImageView(ivWorkoutImageHolder, false);

                setVisibility_TextView(tvFront, false);

                setVisibility_TextView(tvBack, false);
                setVisibility_TextView(tvTimerHolder, true);

                update_WorkoutValuesLastValue();
                update_WorkoutValuesNextValue();
            } });

    }



    /********** Styling Method **********/

    private void setStyle_ForTextView(TextView tv, int style){
        if (Build.VERSION.SDK_INT < 23) {
            tv.setTextAppearance(context, style);
        } else {
            tv.setTextAppearance(style);
        }
    }

    private void setVisibility_ImageView(ImageView ivImage, boolean visible){
        int visibleValue = View.INVISIBLE;;
        if(visible) visibleValue = View.VISIBLE;

        ivImage.setVisibility(visibleValue);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            ivImage.onVisibilityAggregated(visible);
        }
    }

    private void setVisibility_TextView(TextView tv, boolean visible){

        int visibleValue = View.INVISIBLE;;
        if(visible) visibleValue = View.VISIBLE;

        tv.setVisibility(visibleValue);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tv.onVisibilityAggregated(visible);
        }
    }

    /****************************************
     /**** WORKOUT HELPER METHODS
     ****************************************/

    public void show_WorkoutHelper() {
        TextView tvWorkoutDescription = new TextView(context);
        tvWorkoutDescription.setText(workout.get_WorkoutDescription(progress));
        tvWorkoutDescription.setTextColor(context.getResources().getColor(R.color.focusAccent));
        llWorkoutHelper.addView(tvWorkoutDescription);
    }

    public void clear_WorkoutHelper(){
        set_CloseExpandButton();
        llWorkoutHelper.removeAllViews();
    }

    public void set_ExpandButton() {
        btn_WorkoutHelper.setImageDrawable(context.getDrawable(R.drawable.ic_expand_less_black_24dp));
    }

    public void set_CloseExpandButton() {
        btn_WorkoutHelper.setImageDrawable(context.getDrawable(R.drawable.ic_expand_more_black_24dp));
    }

    /****************************************
     /**** FEEDBACK METHODS
     ****************************************/

    public void play_Sound(){
        if(soundOn){
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        }
    }

    public void vibrate(){
        if(vibrateOn){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                vibrator.vibrate(500);
            }
        }
    }
}
