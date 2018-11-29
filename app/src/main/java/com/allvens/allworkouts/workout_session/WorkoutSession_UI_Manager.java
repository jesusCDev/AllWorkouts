package com.allvens.allworkouts.workout_session;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.allvens.allworkouts.workout_session.workouts.Workout;

public class WorkoutSession_UI_Manager {

    private Workout workout;
    private int progress = 0;

    private Context context;
    private TextView tv_WorkoutName;
    private LinearLayout ll_MainScreen;
    private LinearLayout ll_ValueHolder;
    private Button btn_ChangeScreens;

    private TextView tv_Timer;

    RelativeLayout[] rl_BottomValues;
    
    public WorkoutSession_UI_Manager(Context context, TextView tv_workout_workoutName, LinearLayout ll_workout_timeImageHolder, LinearLayout ll_workout_ValueHolder, Button btn_workout_completeTask) {
        this.context = context;
        tv_WorkoutName = tv_workout_workoutName;
        ll_MainScreen = ll_workout_timeImageHolder;
        ll_ValueHolder = ll_workout_ValueHolder;
        btn_ChangeScreens = btn_workout_completeTask;
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
        rl_BottomValues[progress].setBackgroundColor(Color.RED);
    }

    private void update_LastValue(){
        rl_BottomValues[(progress - 1)].setBackgroundColor(Color.TRANSPARENT);
    }

    public void create_BottomValueViews(){
        rl_BottomValues = new RelativeLayout[5];
        for(int i = 0; i < 5; i++){
            rl_BottomValues[i] = create_ValueCounter(workout.get_WorkoutValue(i));
            ll_ValueHolder.addView(rl_BottomValues[i]);
        }
    }

    private RelativeLayout create_ValueCounter(int workoutValue){
        Log.d("Bug", "WokroutValue: "+ workoutValue);
        RelativeLayout rl = new RelativeLayout(context);

        TextView tvValue = new TextView(context);
        tvValue.setText(Integer.toString(workoutValue));

        // todo set style
        rl.addView(tvValue);
        return rl;
    }

    public void changeScreen_Workout() {
        tv_WorkoutName.setText(workout.get_WorkoutName(progress));
        btn_ChangeScreens.setText("Complete");

        // todo update bottom values view
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

        ll_MainScreen.addView(tv_Timer);
    }

    ////////////////////////////////////////////////////// random methods

    private void clear_MainView(){
        ll_MainScreen.removeAllViews();
    }

    public void play_basicSound() {
    }
    public void vibrate() {
    }

}
