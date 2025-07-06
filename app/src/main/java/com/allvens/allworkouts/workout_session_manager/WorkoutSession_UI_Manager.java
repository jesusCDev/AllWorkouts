package com.allvens.allworkouts.workout_session_manager;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.iambedant.text.OutlineTextView;

public class WorkoutSession_UI_Manager {

    private Context context;
    private TextView workoutNameTitle;

    private Button completeButton;

    private ConstraintLayout cTimerRepsWorkoutHolder;

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

    public WorkoutSession_UI_Manager(Context context, Workout workout, TextView workoutNameTitle,
                                     ConstraintLayout cTimerRepsWorkoutHolder,
                                     ImageView ivWorkoutImageHolder, TextView tvTimerHolder,
                                     OutlineTextView tvFront, TextView tvBack, TextView tvValue1,
                                     TextView tvValue2,
                                     TextView tvValue3, TextView tvValue4, TextView tvValue5,
                                     Button completeButton) {
        this.context                 = context;
        this.workoutNameTitle        = workoutNameTitle;
        this.cTimerRepsWorkoutHolder = cTimerRepsWorkoutHolder;
        this.ivWorkoutImageHolder    = ivWorkoutImageHolder;
        this.tvTimerHolder           = tvTimerHolder;
        this.tvFront                 = tvFront;
        this.tvBack                  = tvBack;

        aTvWorkoutValues[0] = tvValue1;
        aTvWorkoutValues[1] = tvValue2;
        aTvWorkoutValues[2] = tvValue3;
        aTvWorkoutValues[3] = tvValue4;
        aTvWorkoutValues[4] = tvValue5;

        this.completeButton = completeButton;

        this.vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        SettingsPrefsManager prefs_manager = new SettingsPrefsManager(context);
        soundOn                            = prefs_manager.getPrefSetting(Preferences_Values.SOUND_ON);
        vibrateOn                          = prefs_manager.getPrefSetting(Preferences_Values.VIBRATE_ON);

        this.workout = workout;

        updateWorkoutValuesTextViews();
    }

    /****************************************
     /**** SETTER/GETTER METHODS
     ****************************************/

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public int getProgress() {
        return progress;
    }

    /****************************************
     /**** UI UPDATERS
     ****************************************/

    public void setTimeTV(long seconds) {
        tvTimerHolder.setText(Long.toString(seconds));
    }

    private void updateWorkoutValuesTextViews() {
        aTvWorkoutValues[0].setText(Integer.toString(workout.getWorkoutValue(0)));
        aTvWorkoutValues[1].setText(Integer.toString(workout.getWorkoutValue(1)));
        aTvWorkoutValues[2].setText(Integer.toString(workout.getWorkoutValue(2)));
        aTvWorkoutValues[3].setText(Integer.toString(workout.getWorkoutValue(3)));
        aTvWorkoutValues[4].setText(Integer.toString(workout.getWorkoutValue(4)));
    }

    private void updateWorkoutValuesNextValue() {
        setStyleForTextView(aTvWorkoutValues[progress], R.style.tv_WorkoutSession_CurrentWorkoutValue);
    }

    private void updateWorkoutValuesLastValue() {
        aTvWorkoutValues[(progress - 1)].setTextColor(context.getResources().getColor(R.color.unSelectedButton));
        setStyleForTextView(aTvWorkoutValues[(progress - 1)], R.style.tv_WorkoutSession_WorkoutValue);
    }

    /****************************************
     /**** SCREEN CHANGERS
     ****************************************/

    public void changeScreenToWorkout() {
        Activity activity = (Activity) context;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                workoutNameTitle.setText(workout.get_WorkoutName(progress));
                completeButton.setText("Complete");

                tvFront.setText(Integer.toString(workout.getWorkoutValue(progress)));
                tvBack.setText(Integer.toString(workout.getWorkoutValue(progress)));

                updateWorkoutValuesNextValue();

                setVisibilityTextView(tvTimerHolder, false);

                setVisibilityTextView(tvFront, true);
                setVisibilityTextView(tvBack, true);

                setVisibilityImageView(ivWorkoutImageHolder, true);
                ivWorkoutImageHolder.setImageResource(workout.get_WorkoutImage(progress));
            }
        });
    }


    public void changeScreenToTimer() {
        Activity activity = (Activity)context;

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                workoutNameTitle.setText(workout.get_WorkoutName(progress));
                completeButton.setText("Next");

                setVisibilityImageView(ivWorkoutImageHolder, false);

                setVisibilityTextView(tvFront, false);
                setVisibilityTextView(tvBack, false);
                setVisibilityTextView(tvTimerHolder, true);

                updateWorkoutValuesLastValue();
                updateWorkoutValuesNextValue();
            }
        });
    }

    /********** Styling Method **********/
    private void setStyleForTextView(TextView tv, int style) {
        if (Build.VERSION.SDK_INT < 23) {
            tv.setTextAppearance(context, style);
        } else {
            tv.setTextAppearance(style);
        }
    }

    private void setVisibilityTextView(TextView textView, boolean visible) {
        textView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    private void setVisibilityImageView(ImageView imageView, boolean visible) {
        imageView.setVisibility(visible ? View.VISIBLE : View.GONE);
    }

    /****************************************
     /**** FEEDBACK METHODS
     ****************************************/

    public void playSound() {
        if(soundOn) {
            toneGen1.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
        }
    }

    public void vibrate() {
        if(vibrateOn) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }
            else{
                vibrator.vibrate(500);
            }
        }
    }
}
