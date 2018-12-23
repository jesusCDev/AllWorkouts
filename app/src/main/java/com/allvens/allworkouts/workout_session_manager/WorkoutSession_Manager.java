package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.WorkoutSessionFinishActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout_Generator;

public class WorkoutSession_Manager {

    private Context context;
    private Workout workout;
    private Workout_Info workout_info;
    private WorkoutSessionScene_UI_Manager workoutSessionUi_manager;
    private Timer timer;

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();

        Workout_Generator workoutGenerator = new Workout_Generator(wrapper.get_Workout(choice));
        workout_info = workoutGenerator.get_WorkoutInfo();
        workout = workoutGenerator.get_Workout();



        DebuggingMethods.pop("Getting Workout");
        DebuggingMethods.pop("Workout: " + workout_info.getWorkout());
        DebuggingMethods.pop("Type: " + workout_info.getType());

        wrapper.close();
    }

    /****************************************
     /**** SETTER METHODS
     ****************************************/

    public void setUp_UiManager(TextView tv_workout_workoutName, LinearLayout llTimeImageHolder, TextView tvValue1, TextView tvValue2, TextView tvValue3, TextView tvValue4, TextView tvValue5, Button btn_workout_completeTask) {
        workoutSessionUi_manager = new WorkoutSessionScene_UI_Manager(context, workout, tv_workout_workoutName, llTimeImageHolder, tvValue1, tvValue2, tvValue3, tvValue4, tvValue5, btn_workout_completeTask);
    }

    public void set_Timer(){
        timer = new Timer(workoutSessionUi_manager);
    }

    /****************************************
     /**** SCREEN METHODS
     ****************************************/

    public void start_Screen(){
        workoutSessionUi_manager.changeScreen_Workout();
    }

    public void update_Screen(){
        if(timer.get_TimerRunning()){
            timer.stop_timer();
            workoutSessionUi_manager.changeScreen_Workout();
        }else{
            workoutSessionUi_manager.set_Progress((workoutSessionUi_manager.get_Progress() + 1));
            if(workoutSessionUi_manager.get_Progress() < 5){
                workoutSessionUi_manager.changeScreen_Timer();
                timer.create_timer(workout.get_BreakTime(workoutSessionUi_manager.get_Progress()));
                timer.start_timer();
            }else{
                Intent intent = new Intent(context, WorkoutSessionFinishActivity.class);
                intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, workout_info.getWorkout());
                context.startActivity(intent);
            }
        }
    }

    /****************************************
     /**** TIMER - KILLER
     ****************************************/

    public void kill_Timer(){
        if(timer.get_TimerRunning()) timer.stop_timer();
    }
}
