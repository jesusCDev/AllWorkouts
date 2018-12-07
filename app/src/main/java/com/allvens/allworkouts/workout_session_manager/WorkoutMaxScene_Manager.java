package com.allvens.allworkouts.workout_session_manager;

import android.content.Context;
import android.widget.TextView;

import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;

public class WorkoutMaxScene_Manager {

    private Context context;

    private int maxValue = 1;
    private String chosenWorkout;
    private int type;

    private TextView tvCounterView;

    public WorkoutMaxScene_Manager(Context context, String chosenWorkout, int type) {
        this.context = context;
        this.chosenWorkout = chosenWorkout;
        this.type = type;
    }

    public void set_tvCounterView(TextView tvCounterView) {
        this.tvCounterView = tvCounterView;
    }

    private void update_Counter(){
        tvCounterView.setText(Integer.toString(maxValue));
    }

    public void update_WorkoutProgress(){
        Workout_Wrapper wrapper = new Workout_Wrapper(context);
        wrapper.open();

        boolean workoutExist = false;
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(chosenWorkout)){
                workout.setProgress(1);
                workout.setMax(maxValue);
                wrapper.update_Workout(workout);
                workoutExist = true;
            }
        }

        if(!workoutExist){
            wrapper.create_Workout(new Workout_Info(chosenWorkout, maxValue, type, 1));
        }

        wrapper.close();
    }

    public void subtract_OneFromMax() {
        if(maxValue != 1){
            maxValue -= 1;
            update_Counter();
        }
    }

    public void add_OneToMax() {
        maxValue += 1;
        update_Counter();
    }

    public void add_FiveToMax() {
        maxValue += 5;
        update_Counter();
    }
}
