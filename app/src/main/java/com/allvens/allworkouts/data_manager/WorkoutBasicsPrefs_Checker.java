package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.ViewGroup;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

public class WorkoutBasicsPrefs_Checker {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;
    private WorkoutPosAndStatus[] turnOffOn_workouts;
    private WorkoutPosAndStatus[] turnOn_workouts;

    public WorkoutBasicsPrefs_Checker(Context context){
        prefs              = context.getSharedPreferences(Preferences_Values.PREFS_NAMES, Context.MODE_PRIVATE);
        edit               = prefs.edit();
        turnOffOn_workouts = get_AllWorkoutsPositionsAndStatus();
        turnOn_workouts    = get_WorkoutsWithOnStatusOnly();
    }

    public WorkoutPosAndStatus[] get_WorkoutsPos(boolean includeOffStatus) {
        if(includeOffStatus){
            return turnOffOn_workouts;
        }

        return turnOn_workouts;
    }

    /********** Gets Workout Sets - for current list  **********/
    private WorkoutPosAndStatus[] get_WorkoutsWithOnStatusOnly(){
        WorkoutPosAndStatus[] onWorkouts;

        int size = 0;

        for(WorkoutPosAndStatus value: turnOffOn_workouts){
            if(value.get_TurnOnStatus()){
                size++;
            }
        }

        onWorkouts = new WorkoutPosAndStatus[size];
        int iter   = 0;

        for(int i = 0; i < turnOffOn_workouts.length; i++){
            if(turnOffOn_workouts[i].get_TurnOnStatus()){
                onWorkouts[iter] = turnOffOn_workouts[i];
                iter++;
            }
        }

        return onWorkouts;
    }

    private WorkoutPosAndStatus[] get_AllWorkoutsPositionsAndStatus(){
        WorkoutPosAndStatus[] workouts = new WorkoutPosAndStatus[5];
        workouts[0]                    = get_WorkoutData(Constants.PULL_UPS, Preferences_Values.PULL_POS, Preferences_Values.PULL_STAT, R.id.pullUpsPosContainer);
        workouts[1]                    = get_WorkoutData(Constants.PUSH_UPS, Preferences_Values.PUSH_POS, Preferences_Values.PUSH_STAT, R.id.pushUpsPosContainer);
        workouts[2]                    = get_WorkoutData(Constants.SIT_UPS , Preferences_Values.SIT_POS , Preferences_Values.SIT_STAT , R.id.sitUpsPosContainer);
        workouts[3]                    = get_WorkoutData(Constants.SQUATS  , Preferences_Values.SQT_POS , Preferences_Values.SQT_STAT , R.id.squatUpsPosContainer);
        workouts[4]                    = get_WorkoutData(Constants.BACK_STRENGTHENING, Preferences_Values.BACK_POS, Preferences_Values.BACK_STAT, R.id.backStrengtheningPosContainer);

        return organize_DataSetByOrder(workouts);
    }

    /********** Setting and Get Workout Pos and Status **********/
    private WorkoutPosAndStatus get_WorkoutData(String workoutName, String posPrefKey, String statusPrefKey, int resourceID){
        WorkoutPosAndStatus workout = new WorkoutPosAndStatus(workoutName, posPrefKey, statusPrefKey, resourceID);

        workout.setPosition(prefs.getInt(posPrefKey, 1));
        workout.set_TurnOnStatus(prefs.getBoolean(statusPrefKey, true));

        return workout;
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

    /**
     * Switches Values to re-arrange array
     */
    private WorkoutPosAndStatus[] switch_DataSetValues(WorkoutPosAndStatus[] dataSet, int beforePos, int afterPos){
        WorkoutPosAndStatus beforeValue = dataSet[beforePos];
        WorkoutPosAndStatus afterValue  = dataSet[afterPos];
        dataSet[beforePos]              = afterValue;
        dataSet[afterPos]               = beforeValue;

        return dataSet;
    }

    /****************************************
     /**** UPDATE POS AND STATUS
     ****************************************/

    public void update_WorkoutStatusPref(String prefKey, boolean value){
        edit.putBoolean(prefKey, value);
        edit.commit();
    }

    public void update_WorkoutPositionPref(String prefKey, int value){
        edit.putInt(prefKey, value);
        edit.commit();
    }

    /**
     * updates position but only when called upon and moved though a drag listener
     * @param parent
     */
    public void update_WorkoutsWithViews(ViewGroup parent) {
        for(WorkoutPosAndStatus workout: turnOffOn_workouts){
            for(int i = 0; i < 5; i++){
                if(parent.getChildAt(i).getId() == workout.getResourceID()){
                    update_WorkoutPositionPref(workout.getPosPrefKey(), parent.indexOfChild(parent.getChildAt(i)));
                }
            }
        }
    }
}
