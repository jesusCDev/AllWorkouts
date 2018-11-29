package com.allvens.allworkouts.workout_session.workouts;

public class PullUps extends Workout{
    
    private final static double FIRST_VALUE_DIFFICULTY = 0.5; // Wide Arm
    private final static double SECOND_VALUE_DIFFICULTY = 0.8; // Front Arm
    private final static double THIRD_VALUE_DIFFICULTY = 0.4; // Side Arm
    private final static double FORTH_VALUE_DIFFICULTY = 0.6; // Back Arm
    private final static double FIFTH_VALUE_DIFFICULTY = 0.3; // One Arm

    private final static String FIRST_WORKOUT_NAME = "Wide Arm";
    private final static String SECOND_WORKOUT_NAME = "Front Arm";
    private final static String THIRD_WORKOUT_NAME = "Side Arm";
    private final static String FORTH_WORKOUT_NAME = "Back Arm";
    private final static String FIFTH_WORKOUT_NAME = "One Arm";

    private final static double[] DIFFICULTY_VALUES = {FIRST_VALUE_DIFFICULTY,
            SECOND_VALUE_DIFFICULTY, THIRD_VALUE_DIFFICULTY, FORTH_VALUE_DIFFICULTY,
            FIFTH_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {FIRST_WORKOUT_NAME,
            SECOND_WORKOUT_NAME, THIRD_WORKOUT_NAME, FORTH_WORKOUT_NAME,
            FIFTH_WORKOUT_NAME};

    private final static String[] IMAGE_RESOURCES_MIX = {"","","","",""};
    private final static String[] IMAGE_RESOURCES_SIMPLE = {"","","","",""};

    public PullUps(double type, int max){
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
