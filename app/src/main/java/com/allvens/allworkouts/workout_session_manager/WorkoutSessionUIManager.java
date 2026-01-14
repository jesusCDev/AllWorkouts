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
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.DifficultyRatingManager;
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.iambedant.text.OutlineTextView;

public class WorkoutSessionUIManager {

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

    // Break-only UI elements
    private LinearLayout llDifficultySlider;
    private SeekBar seekbarDifficulty;
    private Button btnExtraBreak;
    private int extraBreaksRemaining = 3;
    private int selectedDifficulty = DifficultyRatingManager.FEEDBACK_JUST_RIGHT; // Default: normal
    private ExtraBreakCallback extraBreakCallback;

    public interface ExtraBreakCallback {
        void onExtraBreakRequested(int seconds);
    }

    boolean soundOn;
    boolean vibrateOn;
    private Vibrator vibrator;
    private ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

    private int progress = 0;

    public WorkoutSessionUIManager(
            Context context, Workout workout, TextView workoutNameTitle,
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
        soundOn                            = prefs_manager.getPrefSetting(PreferencesValues.SOUND_ON);
        vibrateOn                          = prefs_manager.getPrefSetting(PreferencesValues.VIBRATE_ON);

        this.workout = workout;

        // Find break-only UI elements
        Activity activity = (Activity) context;
        llDifficultySlider = activity.findViewById(R.id.ll_difficulty_slider);
        seekbarDifficulty = activity.findViewById(R.id.seekbar_difficulty);
        btnExtraBreak = activity.findViewById(R.id.btn_extra_break);

        // Setup difficulty slider listener
        if (seekbarDifficulty != null) {
            seekbarDifficulty.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    // 0 = easier (TOO_EASY), 1 = normal (JUST_RIGHT), 2 = harder (TOO_HARD)
                    switch (progress) {
                        case 0:
                            selectedDifficulty = DifficultyRatingManager.FEEDBACK_TOO_EASY;
                            break;
                        case 2:
                            selectedDifficulty = DifficultyRatingManager.FEEDBACK_TOO_HARD;
                            break;
                        default:
                            selectedDifficulty = DifficultyRatingManager.FEEDBACK_JUST_RIGHT;
                            break;
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
        }

        // Setup extra break button listener
        if (btnExtraBreak != null) {
            updateExtraBreakButton();
            btnExtraBreak.setOnClickListener(v -> {
                if (extraBreaksRemaining > 0 && extraBreakCallback != null) {
                    extraBreaksRemaining--;
                    extraBreakCallback.onExtraBreakRequested(30);
                    updateExtraBreakButton();
                }
            });
        }

        updateWorkoutValuesTextViews();
    }

    /**
     * Set callback for extra break requests
     */
    public void setExtraBreakCallback(ExtraBreakCallback callback) {
        this.extraBreakCallback = callback;
    }

    /**
     * Update extra break button text and state
     */
    private void updateExtraBreakButton() {
        if (btnExtraBreak != null) {
            if (extraBreaksRemaining > 0) {
                btnExtraBreak.setText("+30s Extra Break (" + extraBreaksRemaining + " left)");
                btnExtraBreak.setEnabled(true);
                btnExtraBreak.setAlpha(1.0f);
            } else {
                btnExtraBreak.setText("No Extra Breaks Left");
                btnExtraBreak.setEnabled(false);
                btnExtraBreak.setAlpha(0.5f);
            }
        }
    }

    /**
     * Get the selected difficulty from the slider
     * @return DifficultyRatingManager feedback constant
     */
    public int getSelectedDifficulty() {
        return selectedDifficulty;
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

                setVisibilityTextView(tvFront, false); // todo - hot fix
                setVisibilityTextView(tvBack, true);

                setVisibilityImageView(ivWorkoutImageHolder, true);
                ivWorkoutImageHolder.setImageResource(workout.get_WorkoutImage(progress));

                // Hide break-only UI elements
                hideBreakOnlyElements();
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

                // Show break-only UI elements
                showBreakOnlyElements();
            }
        });
    }

    /**
     * Show break-only UI elements (difficulty slider and extra break button)
     */
    private void showBreakOnlyElements() {
        // Check settings preferences
        SettingsPrefsManager prefs = new SettingsPrefsManager(context);
        boolean showDifficultySlider = prefs.getPrefSetting(PreferencesValues.SHOW_DIFFICULTY_SLIDER, true);
        boolean showExtraBreak = prefs.getPrefSetting(PreferencesValues.SHOW_EXTRA_BREAK, true);
        
        if (llDifficultySlider != null && showDifficultySlider) {
            llDifficultySlider.setVisibility(View.VISIBLE);
        }
        if (btnExtraBreak != null && showExtraBreak && extraBreaksRemaining > 0) {
            btnExtraBreak.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Hide break-only UI elements
     */
    private void hideBreakOnlyElements() {
        if (llDifficultySlider != null) {
            llDifficultySlider.setVisibility(View.GONE);
        }
        if (btnExtraBreak != null) {
            btnExtraBreak.setVisibility(View.GONE);
        }
    }

    /********** Styling Method **********/
    private void setStyleForTextView(TextView tv, int style) {
        if(Build.VERSION.SDK_INT < 23) {
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
