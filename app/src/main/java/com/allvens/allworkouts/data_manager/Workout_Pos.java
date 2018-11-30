package com.allvens.allworkouts.data_manager;

import android.content.Context;
import android.content.SharedPreferences;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.assets.PreferencesValues;

public class Workout_Pos {

    private SharedPreferences prefs;
    private SharedPreferences.Editor edit;


    public Workout_Pos(Context context){
        prefs = context.getSharedPreferences(PreferencesValues.PREFS_NAMES, Context.MODE_PRIVATE);
        edit = prefs.edit();
    }

    public String[][] get_AllWorkoutsAndPositions(boolean includeZeroPosition){
        String[][] workoutsAndPos = new String[4][2];

        int dataBasePos = 0;
        dataBasePos = set_Data(workoutsAndPos, dataBasePos, includeZeroPosition, PreferencesValues.PULL_POS, Constants.PULL_UPS);
        dataBasePos = set_Data(workoutsAndPos, dataBasePos, includeZeroPosition, PreferencesValues.PUSH_POS, Constants.PUSH_UPS);
        dataBasePos = set_Data(workoutsAndPos, dataBasePos, includeZeroPosition, PreferencesValues.SIT_POS, Constants.SIT_UPS);
        set_Data(workoutsAndPos, dataBasePos, includeZeroPosition, PreferencesValues.SQT_POS, Constants.SQUATS);

        return organize_DataSet(fix_DataBase(workoutsAndPos));
    }

    private String[][] fix_DataBase(String[][] database){
        int dataBasePos = 0;
        for(int i = 0; i < database.length; i++){
            if(database[i][0] != null){
                dataBasePos++;
            }
        }

        String[][] newDataBase = new String[dataBasePos][2];
        for(int i = 0; i < dataBasePos; i++){
            newDataBase[i][0] = database[i][0];
            newDataBase[i][1] = database[i][1];
        }

        return newDataBase;
    }

    private String[][] organize_DataSet(String[][] dataSet){
        String[][] newDataSet = dataSet;

        for(int pos = 1; pos < dataSet.length; pos++){
            if(check_beforeValuePosToNextValue(dataSet[(pos - 1)][1], dataSet[pos][1])){
                newDataSet = organize_DataSet(switch_DataSetValues(dataSet, (pos-1), pos));
                break;
            }
        }

        return newDataSet;
    }

    private String[][] switch_DataSetValues(String[][] dataSet, int beforeValue, int afterValue){
        String beforeValue0 = dataSet[beforeValue][0];
        String beforeValue1 = dataSet[beforeValue][1];
        String newValue0 = dataSet[afterValue][0];
        String newValue1 = dataSet[afterValue][1];

        dataSet[beforeValue][0] = newValue0;
        dataSet[beforeValue][1] = newValue1;

        dataSet[afterValue][0] = beforeValue0;
        dataSet[afterValue][1] = beforeValue1;

        return dataSet;
    }

    private boolean check_beforeValuePosToNextValue(String beforeValue, String afterValue){
        return (Integer.parseInt(afterValue) < Integer.parseInt(beforeValue));
    }

    public int set_Data(String[][] database, int databasePos, boolean includeZeroPosition, String prefKey, String workout){
        if((prefs.getInt(prefKey, 1) > 0) || (includeZeroPosition)){
            database[databasePos][0] = workout;
            database[databasePos][1] = Integer.toString(prefs.getInt(prefKey, 1));
            return (databasePos + 1);
        }
        return databasePos;
    }

    public void update_WorkoutPosition(String prefKey, int value){
        edit.putInt(prefKey,value);
        edit.commit();
    }
}
