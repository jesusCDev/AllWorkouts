package com.allvens.allworkouts.workout_session;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.WorkoutSessionFinishActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.workout_session.workouts.PullUps;
import com.allvens.allworkouts.workout_session.workouts.PushUps;
import com.allvens.allworkouts.workout_session.workouts.SitUps;
import com.allvens.allworkouts.workout_session.workouts.Squats;
import com.allvens.allworkouts.workout_session.workouts.Workout;

public class WorkoutSession_Manager {

    private Context context;
    private Workout workout;
    private Workout_Info workout_info;
    private WorkoutSession_UI_Manager workoutSessionUi_manager;
    private Timer timer;

    private void pop(String message){
        Log.d("Bug", message);
    }

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        pop("Workout Choice: " + choice);

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
                workout = new PullUps(workout_info.getType(), workout_info.getMax());
                break;
            case Constants.PUSH_UPS:
                workout = new PushUps(workout_info.getType(), workout_info.getMax());
                break;
            case Constants.SIT_UPS:
                workout = new SitUps(workout_info.getType(), workout_info.getMax());
                break;
            default:
                workout = new Squats(workout_info.getType(), workout_info.getMax());
                break;
        }

        workoutSessionUi_manager = new WorkoutSession_UI_Manager(context, tv_workout_WorkoutName, ll_workout_timeImageHolder, ll_workout_ValueHolder, btn_workout_CompleteTask);
        workoutSessionUi_manager.set_Workout(workout);
    }

    public void start_Screen(){
        workoutSessionUi_manager.create_BottomValueViews();
        workoutSessionUi_manager.changeScreen_Workout();
    }

    public void setUp_Timer(){
        timer = new Timer(workoutSessionUi_manager);
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
}
