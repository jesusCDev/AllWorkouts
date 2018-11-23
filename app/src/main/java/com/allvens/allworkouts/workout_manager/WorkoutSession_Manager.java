package com.allvens.allworkouts.workout_manager;

import android.content.Context;
import android.content.Intent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.WorkoutFinishActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;
import com.allvens.allworkouts.workout_manager.workouts.PullUps;
import com.allvens.allworkouts.workout_manager.workouts.Workout;

public class WorkoutSession_Manager {

    private Context context;
    private Workout workout;
    private Workout_Info workout_info;
    private UI_Manager ui_manager;
    private Timer timer;

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        // get workout info
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(choice)){
                this.workout_info = workout;
            }
        }
        wrapper.close();
    }

    public void setUp_UiManager(TextView tv_workout_WorkoutName, LinearLayout ll_workout_timeImageHolder, LinearLayout ll_workout_ValueHolder, Button btn_workout_CompleteTask){
        // create workout
        switch (workout_info.getWorkout()){
            case Constants.PULL_UPS:
                workout = new PullUps(workout_info.getType());
                break;
            case Constants.PUSH_UPS:
                break;
            case Constants.SIT_UPS:
                break;
            case Constants.SQUATS:
                break;
        }

        // set workout values
        workout.set_Max(workout_info.getMax());

        ui_manager = new UI_Manager(context, tv_workout_WorkoutName, ll_workout_timeImageHolder, ll_workout_ValueHolder, btn_workout_CompleteTask);
        ui_manager.set_Workout(workout);
    }

    public void start_Screen(){
        ui_manager.create_BottomValueViews();
        ui_manager.changeScreen_Workout();
    }

    public void setUp_Timer(){
        timer = new Timer(ui_manager);
    }

    public void update_Screen(){
        if(timer.get_TimerRunning()){
            timer.stop_timer();
            ui_manager.changeScreen_Workout();
        }else{
            ui_manager.set_Progress((ui_manager.get_Progress() + 1));
            if(ui_manager.get_Progress() < 5){
                ui_manager.changeScreen_Timer();
                timer.create_timer(workout.get_BreakTime(ui_manager.get_Progress()));
                timer.start_timer();
            }else{
                Intent intent = new Intent(context, WorkoutFinishActivity.class);
                intent.putExtra(Constants.CHOOSEN_WORKOUT_EXTRA_KEY, workout_info.getWorkout());
                context.startActivity(intent);
            }
        }
    }
}
