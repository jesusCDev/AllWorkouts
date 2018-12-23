package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class Squats extends Workout{

    private final static double FIRST_VALUE_DIFFICULTY = 0.6; // Punching
    private final static double SECOND_VALUE_DIFFICULTY = 0.8; // Normal
    private final static double THIRD_VALUE_DIFFICULTY = 0.6; // Kicking
    private final static double FORTH_VALUE_DIFFICULTY = 0.7; // Pile
    private final static double FIFTH_VALUE_DIFFICULTY = 0.4; // Jumping

    private final static String FIRST_WORKOUT_NAME = "Punching";
    private final static String SECOND_WORKOUT_NAME = "Normal";
    private final static String THIRD_WORKOUT_NAME = "Kicking";
    private final static String FORTH_WORKOUT_NAME = "Pile";
    private final static String FIFTH_WORKOUT_NAME = "Jumping";

    private final static double[] DIFFICULTY_VALUES = {FIRST_VALUE_DIFFICULTY,
            SECOND_VALUE_DIFFICULTY, THIRD_VALUE_DIFFICULTY, FORTH_VALUE_DIFFICULTY,
            FIFTH_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {FIRST_WORKOUT_NAME,
            SECOND_WORKOUT_NAME, THIRD_WORKOUT_NAME, FORTH_WORKOUT_NAME,
            FIFTH_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.ic_workout_pullup_wide_arm,
            R.drawable.ic_workout_pullup_front_arm,R.drawable.ic_workout_pullup_side_arm,
            R.drawable.ic_workout_pullup_back_arm, R.drawable.ic_workout_pullup_one_arm};
    private final static int[] IMAGE_RESOURCES_SIMPLE = {R.drawable.ic_workout_pullup_normal,
            R.drawable.ic_workout_pullup_normal,R.drawable.ic_workout_pullup_normal,
            R.drawable.ic_workout_pullup_normal,R.drawable.ic_workout_pullup_normal};

    public Squats(double type, int max){
        if(type == 0){
            set_DifficultyValues(DIFFICULTY_VALUES);
            set_ImageResources(IMAGE_RESOURCES_MIX);
            set_WorkoutName(WORKOUT_NAMES);
        }else{
            set_DifficultyValues(Workouts_SimpleValues.DIFFICULTY_VALUES);
            set_ImageResources(IMAGE_RESOURCES_SIMPLE);
            set_WorkoutName(new String[]{"Normal","Normal","Normal","Normal","Normal"});
        }

        set_Max(max);
        create_WorkoutValues();
        create_BreakTimes();
    }
}
