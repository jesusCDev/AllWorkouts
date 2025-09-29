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
    private WorkoutPosAndStatus[] allWorkouts;
    private WorkoutPosAndStatus[] enabledWorkouts;

    public WorkoutBasicsPrefs_Checker(Context context){
        prefs              = context.getSharedPreferences(Preferences_Values.PREFS_NAMES, Context.MODE_PRIVATE);
        edit               = prefs.edit();
        allWorkouts = getAllWorkoutsPositionsAndStatus();
        enabledWorkouts    = getWorkoutsWithOnStatusOnly();
    }

    public WorkoutPosAndStatus[] getWorkoutPositions(boolean includeDisabled) {
        if(includeDisabled){
            return allWorkouts;
        }

        return enabledWorkouts;
    }

    /********** Gets Workout Sets - for current list  **********/
    private WorkoutPosAndStatus[] getWorkoutsWithOnStatusOnly(){
        WorkoutPosAndStatus[] onWorkouts;

        int size = 0;

        for(WorkoutPosAndStatus value: allWorkouts){
            if(value.get_TurnOnStatus()){
                size++;
            }
        }

        onWorkouts = new WorkoutPosAndStatus[size];
        int iter   = 0;

        for(int i = 0; i < allWorkouts.length; i++){
            if(allWorkouts[i].get_TurnOnStatus()){
                onWorkouts[iter] = allWorkouts[i];
                iter++;
            }
        }

        return onWorkouts;
    }

    private WorkoutPosAndStatus[] getAllWorkoutsPositionsAndStatus(){
        WorkoutPosAndStatus[] workouts = new WorkoutPosAndStatus[5];
        workouts[0]                    = getWorkoutData(Constants.PULL_UPS, Preferences_Values.PULL_POS, Preferences_Values.PULL_STAT, R.id.pullUpsPosContainer);
        workouts[1]                    = getWorkoutData(Constants.PUSH_UPS, Preferences_Values.PUSH_POS, Preferences_Values.PUSH_STAT, R.id.pushUpsPosContainer);
        workouts[2]                    = getWorkoutData(Constants.SIT_UPS , Preferences_Values.SIT_POS , Preferences_Values.SIT_STAT , R.id.sitUpsPosContainer);
        workouts[3]                    = getWorkoutData(Constants.SQUATS  , Preferences_Values.SQT_POS , Preferences_Values.SQT_STAT , R.id.squatUpsPosContainer);
        workouts[4]                    = getWorkoutData(Constants.BACK_STRENGTHENING, Preferences_Values.BACK_POS, Preferences_Values.BACK_STAT, R.id.backStrengtheningPosContainer);

        return organizeDataSetByOrder(workouts);
    }

    /********** Setting and Get Workout Pos and Status **********/
    private WorkoutPosAndStatus getWorkoutData(String workoutName, String posPrefKey, String statusPrefKey, int resourceID){
        WorkoutPosAndStatus workout = new WorkoutPosAndStatus(workoutName, posPrefKey, statusPrefKey, resourceID);

        workout.setPosition(prefs.getInt(posPrefKey, 1));
        workout.set_TurnOnStatus(prefs.getBoolean(statusPrefKey, true));

        return workout;
    }


    private WorkoutPosAndStatus[] organizeDataSetByOrder(WorkoutPosAndStatus[] dataSet){
        WorkoutPosAndStatus[] newDataSet = dataSet;

        for(int pos = 1; pos < dataSet.length; pos++){
            if(dataSet[(pos - 1)].getPosition() > dataSet[pos].getPosition()){
                newDataSet = organizeDataSetByOrder(switchDataSetValues(dataSet, (pos-1), pos));
                break;
            }
        }

        return newDataSet;
    }

    /**
     * Switches Values to re-arrange array
     */
    private WorkoutPosAndStatus[] switchDataSetValues(WorkoutPosAndStatus[] dataSet, int beforePos, int afterPos){
        WorkoutPosAndStatus beforeValue = dataSet[beforePos];
        WorkoutPosAndStatus afterValue  = dataSet[afterPos];
        dataSet[beforePos]              = afterValue;
        dataSet[afterPos]               = beforeValue;

        return dataSet;
    }

    /****************************************
     /**** UPDATE POS AND STATUS
     ****************************************/

    public void updateWorkoutStatusPreference(String prefKey, boolean value){
        edit.putBoolean(prefKey, value);
        edit.commit();
    }

    public void updateWorkoutPositionPreference(String prefKey, int value){
        edit.putInt(prefKey, value);
        edit.commit();
    }

    /**
     * updates position but only when called upon and moved though a drag listener
     * @param parent
     */
    public void updateWorkoutsWithViews(ViewGroup parent) {
        for(WorkoutPosAndStatus workout: allWorkouts){
            for(int i = 0; i < 5; i++){
                if(parent.getChildAt(i).getId() == workout.getResourceID()){
                    updateWorkoutPositionPreference(workout.getPosPrefKey(), parent.indexOfChild(parent.getChildAt(i)));
                }
            }
        }
    }
}
