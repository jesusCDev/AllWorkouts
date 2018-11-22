package com.allvens.allworkouts.workout_manager.workouts_mix;

import com.allvens.allworkouts.workout_manager.Workout;

public class PullUps extends Workout{
    
    private final static double FIRST_VALUE_DIFFICULTY = 0.3;
    private final static double SECOND_VALUE_DIFFICULTY = 0.5;
    private final static double THIRD_VALUE_DIFFICULTY = 0.25;
    private final static double FORTH_VALUE_DIFFICULTY = 0.5;
    private final static double FIFTH_VALUE_DIFFICULTY = 0.2;

    private final static double[] DIFFICULTY_VALUES = {FIRST_VALUE_DIFFICULTY,
            SECOND_VALUE_DIFFICULTY, THIRD_VALUE_DIFFICULTY, FORTH_VALUE_DIFFICULTY,
            FIFTH_VALUE_DIFFICULTY};

    private final static String[] IMAGE_RESOURCES_MIX = {"","",""};
    private final static String[] IMAGE_RESOURCES_SIMPLE = {"","",""};

    public PullUps(double type){

        if(type == 0){
            set_DifficultyValues(DIFFICULTY_VALUES);
            set_ImageResources(IMAGE_RESOURCES_MIX);
        }else{
            set_DifficultyValues(Workouts_SimpleValues.DIFFICULTY_VALUES);
            set_ImageResources(IMAGE_RESOURCES_SIMPLE);
        }
    }
}
