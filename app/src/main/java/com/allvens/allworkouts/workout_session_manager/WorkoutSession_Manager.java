package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.workout_session_manager.workouts.Workout;
import com.allvens.allworkouts.workout_session_manager.workouts.WorkoutGenerator;
import com.iambedant.text.OutlineTextView;

public class WorkoutSession_Manager {

    private Context context;
    private Workout workout;
    private WorkoutInfo workoutInfo;
    private WorkoutSession_UI_Manager workoutSessionUi_manager;
    private Timer timer;

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        wrapper.open();

        WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.getWorkout(choice));
        workoutInfo = workoutGenerator.getWorkoutInfo();
        workout = workoutGenerator.getWorkout();

        wrapper.close();
    }

    /****************************************
     /**** SETTER/GETTER METHODS
     ****************************************/

    public void setUp_UiManager(TextView tv_workout_workoutName, ConstraintLayout cTimerRepsWorkoutHolder,  ImageView ivWorkoutImageHolder, TextView tvTimerHolder,
                                OutlineTextView tvFront, TextView tvBack,
                                TextView tvValue1, TextView tvValue2, TextView tvValue3,
                                TextView tvValue4, TextView tvValue5,
                                Button btn_ChangeScreens) {

        workoutSessionUi_manager = new WorkoutSession_UI_Manager(context, workout, tv_workout_workoutName,
                cTimerRepsWorkoutHolder,ivWorkoutImageHolder, tvTimerHolder,
                tvFront, tvBack, tvValue1, tvValue2, tvValue3, tvValue4, tvValue5,
                btn_ChangeScreens);
    }

    public String getWorkout(){
        return workoutInfo.getWorkout();
    }

    public boolean check_IfFinished() {
        if(!timer.get_TimerRunning()) workoutSessionUi_manager.setProgress((workoutSessionUi_manager.getProgress() + 1));
        return (workoutSessionUi_manager.getProgress() < 5);
    }

    public void set_Timer(){
        timer = new Timer(workoutSessionUi_manager);
    }

    /****************************************
     /**** SCREEN UPDATING
     ****************************************/

    public void start_Screen(){
        workoutSessionUi_manager.changeScreenToWorkout();
    }

    public void update_Screen(){
        if(timer.get_TimerRunning()){
            timer.stop_timer();
            workoutSessionUi_manager.changeScreenToWorkout();
        }else{
            workoutSessionUi_manager.changeScreenToTimer();
            timer.create_timer(workout.get_BreakTime(workoutSessionUi_manager.getProgress()));
            timer.start_timer();
        }
    }

    /****************************************
     /**** TIMER - KILLER
     ****************************************/

    public void kill_Timer(){
        if(timer.get_TimerRunning()) timer.stop_timer();
    }
}
