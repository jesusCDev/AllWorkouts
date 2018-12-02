package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.settings_manager.WorkoutPosAndStatus;

public class Workout_Pos {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    WorkoutPosAndStatus[] workouts;

    public Workout_Pos(Context context){
        prefs = context.getSharedPreferences(PreferencesValues.PREFS_NAMES, Context.MODE_PRIVATE);
        edit = prefs.edit();

        workouts = get_AllWorkoutsPositionsAndStatus();
    }

    public WorkoutPosAndStatus[] get_WorkoutsPos(boolean includeOffStatus) {
        if(includeOffStatus){
            return workouts;
        }else{
            WorkoutPosAndStatus[] onWorkouts;

            int size = 0;
            for(WorkoutPosAndStatus value: workouts){
                if(value.get_TurnOnStatus()){
                    size++;
                }
            }
            onWorkouts = new WorkoutPosAndStatus[size];

            int iter = 0;
            for(int i = 0; i < workouts.length; i++){
                if(workouts[i].get_TurnOnStatus()){
                    onWorkouts[iter] = workouts[i];
                    iter++;
                }
            }
            return onWorkouts;
        }
    }

    private WorkoutPosAndStatus[] get_AllWorkoutsPositionsAndStatus(){
        WorkoutPosAndStatus[] workouts = new WorkoutPosAndStatus[4];
        workouts[0] = get_WorkoutData(Constants.PULL_UPS, PreferencesValues.PULL_POS, PreferencesValues.PULL_STAT, "random1");
        workouts[1] = get_WorkoutData(Constants.PUSH_UPS, PreferencesValues.PUSH_POS, PreferencesValues.PUSH_STAT, "random2");
        workouts[2] = get_WorkoutData(Constants.SIT_UPS, PreferencesValues.SIT_POS, PreferencesValues.SIT_STAT, "random3");
        workouts[3] = get_WorkoutData(Constants.SQUATS, PreferencesValues.SQT_POS, PreferencesValues.SQT_STAT, "random4");
        return organize_DataSetByOrder(workouts);
    }

    private WorkoutPosAndStatus get_WorkoutData(String workoutName, String posPrefKey, String statusPrefKey, String resoruceId){
        WorkoutPosAndStatus workout = new WorkoutPosAndStatus(workoutName, posPrefKey, statusPrefKey, resoruceId);
        workout.setPosition(prefs.getInt(posPrefKey, 1));
        workout.setTurnOnStatus(prefs.getBoolean(statusPrefKey, true));
        return workout;
    }

    public void update_WorkoutPositionPref(String prefKey, int value){
        edit.putInt(prefKey,value);
        edit.commit();
    }

    private WorkoutPosAndStatus[] organize_DataSetByOrder(WorkoutPosAndStatus[] dataSet){
        WorkoutPosAndStatus[] newDataSet = dataSet;

        for(int pos = 1; pos < dataSet.length; pos++){
            if(dataSet[(pos - 1)].getPosition() > dataSet[pos].getPosition()){
                newDataSet = organize_DataSetByOrder(switch_DataSetValues(dataSet, (pos-1), pos));
                break;
            }
        }

        return newDataSet;
    }

    private WorkoutPosAndStatus[] switch_DataSetValues(WorkoutPosAndStatus[] dataSet, int beforePos, int afterPos){

        WorkoutPosAndStatus beforeValue = dataSet[beforePos];
        WorkoutPosAndStatus afterValue = dataSet[afterPos];

        dataSet[beforePos] = afterValue;
        dataSet[afterPos] = beforeValue;
        return dataSet;
    }
}
