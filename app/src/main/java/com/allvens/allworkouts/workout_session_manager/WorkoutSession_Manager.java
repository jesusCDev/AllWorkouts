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
    private WorkoutInfo workout_info;
    private WorkoutSession_UI_Manager workoutSessionUi_manager;
    private Timer timer;

    public WorkoutSession_Manager(Context context, String choice){
        this.context = context;

        WorkoutWrapper wrapper = new WorkoutWrapper(context);
        wrapper.open();

        WorkoutGenerator workoutGenerator = new WorkoutGenerator(wrapper.get_Workout(choice));
        workout_info = workoutGenerator.get_WorkoutInfo();
        workout = workoutGenerator.get_Workout();

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

    public String get_Workout(){
        return workout_info.getWorkout();
    }

    public boolean check_IfFinished() {
        if(!timer.get_TimerRunning()) workoutSessionUi_manager.set_Progress((workoutSessionUi_manager.get_Progress() + 1));
        return (workoutSessionUi_manager.get_Progress() < 5);
    }

    public void set_Timer(){
        timer = new Timer(workoutSessionUi_manager);
    }

    /****************************************
     /**** SCREEN UPDATING
     ****************************************/

    public void start_Screen(){
        workoutSessionUi_manager.changeScreen_Workout();
    }

    public void update_Screen(){
        if(timer.get_TimerRunning()){
            timer.stop_timer();
            workoutSessionUi_manager.changeScreen_Workout();
        }else{
            workoutSessionUi_manager.changeScreen_Timer();
            timer.create_timer(workout.get_BreakTime(workoutSessionUi_manager.get_Progress()));
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
