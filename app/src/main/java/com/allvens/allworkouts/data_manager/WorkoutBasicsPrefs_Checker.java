package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ViewGroup;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

public class WorkoutBasicsPrefs_Checker {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    WorkoutPosAndStatus[] workouts;

    public WorkoutBasicsPrefs_Checker(Context context){
        prefs = context.getSharedPreferences(Preferences_Values.PREFS_NAMES, Context.MODE_PRIVATE);
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
        workouts[0] = get_WorkoutData(Constants.PULL_UPS, Preferences_Values.PULL_POS, Preferences_Values.PULL_STAT, R.id.pullUpsPosContainer);
        workouts[1] = get_WorkoutData(Constants.PUSH_UPS, Preferences_Values.PUSH_POS, Preferences_Values.PUSH_STAT, R.id.pushUpsPosContainer);
        workouts[2] = get_WorkoutData(Constants.SIT_UPS, Preferences_Values.SIT_POS, Preferences_Values.SIT_STAT, R.id.sitUpsPosContainer);
        workouts[3] = get_WorkoutData(Constants.SQUATS, Preferences_Values.SQT_POS, Preferences_Values.SQT_STAT, R.id.squatUpsPosContainer);
        return organize_DataSetByOrder(workouts);
    }

    private WorkoutPosAndStatus get_WorkoutData(String workoutName, String posPrefKey, String statusPrefKey, int resoruceId){
        WorkoutPosAndStatus workout = new WorkoutPosAndStatus(workoutName, posPrefKey, statusPrefKey, resoruceId);
        workout.setPosition(prefs.getInt(posPrefKey, 1));
        workout.set_TurnOnStatus(prefs.getBoolean(statusPrefKey, true));
        return workout;
    }

    public void update_WorkoutStatusPref(String prefKey, boolean value){
        edit.putBoolean(prefKey, value);
        edit.commit();
    }

    public void update_WorkoutPositionPref(String prefKey, int value){
        edit.putInt(prefKey, value);
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

    public void update_WorkoutsWithViews(ViewGroup parent) {
        for(WorkoutPosAndStatus workout: workouts){
            for(int i = 0; i < 4; i++){
                if(parent.getChildAt(i).getId() == workout.getResourceID()){
                    update_WorkoutPositionPref(workout.getPosPrefKey(), parent.indexOfChild(parent.getChildAt(i)));
                }
            }
        }
    }
}
