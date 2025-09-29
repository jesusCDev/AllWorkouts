package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class PullUps extends Workout{

    // lower means harder
    private final static double WIDE_ARM_VALUE_DIFFICULTY = 0.4; // Wide Arm
    private final static double CHIN_UP_VALUE_DIFFICULTY = 0.5; // Back Arm
    private final static double SIDE_ARM_VALUE_DIFFICULTY = 0.4; // Side Arm
    private final static double PULL_UP_VALUE_DIFFICULTY = 0.7; // Pull Up
    private final static double ONE_ARM_VALUE_DIFFICULTY = 0.2; // One Arm

    private final static String WIDE_ARM_WORKOUT_NAME = "Wide Arm Pull Up";
    private final static String CHIN_UP_WORKOUT_NAME = "Chin Up";
    private final static String SIDE_ARM_WORKOUT_NAME = "Side Arm Pull Up";
    private final static String PULL_UP_WORKOUT_NAME = "Pull Up";
    private final static String ONE_ARM_WORKOUT_NAME = "One Arm Pull Up";

    private final static String WIDE_ARM_DES = "1. Hold on to the bar with your hands(fingers) facing forward.\n" +
            "2. Grip the bar with your hands wider then a normal pull up.\n" +
            "3. Pull yourself up, to the point where your chin passes the bar..\n" +
            "4. Make sure not to swing yourself up.\n" +
            "5. Stay in that position for a couple seconds.\n" +
            "6. Lower yourself back down.\n" +
            "7. Repeat steps 3-6 as the number of reps required.";
    private final static String CHIN_UP = "1. Hold on to the bar with your hands(fingers) facing backwards.\n" +
            "2. Grip the bar with your hands a little wider then where your shoulders are.\n" +
            "3. Pull yourself up, to the point where your chin passes the bar.\n" +
            "4. Make sure not to swing yourself up.\n" +
            "5. Stay in that position for a couple seconds.\n" +
            "6. Lower yourself back down.\n" +
            "7. Repeat steps 3-6 as the number of reps required.";
    private final static String SIDE_ARM_DES = "1. Hold on to the bar with your hands(fingers) facing forward.\n" +
            "2. Grip the bar with your hands a little wider then where your shoulders are.\n" +
            "3. Pull yourself up, to the point where your chin passes the bar..\n" +
            "4. Make sure not to swing yourself up.\n" +
            "5. Pull yourself toward either side by bending one elbow and stretching the other.\n" +
            "6. Pull yourself back to the center.\n" +
            "7. Repeat steps 5-6 for the other side if possible or just alternate sides for next rep.\n" +
            "8. Lower yourself back down.\n" +
            "9. Repeat steps 3-8 as the number of reps required.";
    private final static String PUll_UP_DES = "1. Hold on to the bar with your hands(fingers) facing forward.\n" +
            "2. Grip the bar with your hands a little wider then where your shoulders are.\n" +
            "3. Pull yourself up, to the point where your chin passes the bar..\n" +
            "4. Make sure not to swing yourself up.\n" +
            "5. Stay in that position for a couple seconds.\n" +
            "6. Lower yourself back down.\n" +
            "7. Repeat steps 3-6 as the number of reps required.";
    private final static String ONE_ARM_DES = "1. Hold on to the bar with your hand(fingers) facing backwards.\n" +
            "2. Grip the bar with your hand almost parallel to your head.\n" +
            "3. Use a rag to assist you by rapping it around the bar and holding it with your other hand.\n" +
            "4. Pull yourself up, to the point where your chin passes the bar..\n" +
            "5. Make sure not to swing yourself up.\n" +
            "6. Stay in that position for a couple seconds.\n" +
            "7. Lower yourself back down.\n" +
            "8. Repeat steps 4-7 as the number of reps required.";

    private final static String[] WORKOUT_DESCRIPTIONS_MIX = {WIDE_ARM_DES, CHIN_UP, SIDE_ARM_DES, PUll_UP_DES, ONE_ARM_DES};

    private final static double[] DIFFICULTY_VALUES = {WIDE_ARM_VALUE_DIFFICULTY,
            CHIN_UP_VALUE_DIFFICULTY, SIDE_ARM_VALUE_DIFFICULTY, PULL_UP_VALUE_DIFFICULTY,
            ONE_ARM_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {WIDE_ARM_WORKOUT_NAME,
            CHIN_UP_WORKOUT_NAME, SIDE_ARM_WORKOUT_NAME, PULL_UP_WORKOUT_NAME,
            ONE_ARM_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.ic_pullup_widearm,
            R.drawable.ic_pullup_chinup,R.drawable.ic_pullup_sidetoside,
            R.drawable.pull_up_android, R.drawable.ic_pullup_onearm};

    public PullUps(int type, int max){
        if(type == 0){
            set_DifficultyValues(Workouts_SimpleValues.DIFFICULTY_VALUES);

            set_ImageResources(new int[] {R.drawable.pull_up_android,
                    R.drawable.pull_up_android,R.drawable.pull_up_android,
                    R.drawable.pull_up_android,R.drawable.pull_up_android});

            set_WorkoutName(new String[]{PULL_UP_WORKOUT_NAME, PULL_UP_WORKOUT_NAME, PULL_UP_WORKOUT_NAME,
                    PULL_UP_WORKOUT_NAME, PULL_UP_WORKOUT_NAME});

            set_WorkoutDescriptions(new String[]{PUll_UP_DES, PUll_UP_DES, PUll_UP_DES, PUll_UP_DES,
                    PUll_UP_DES});
        }else{
            set_DifficultyValues(DIFFICULTY_VALUES);
            set_ImageResources(IMAGE_RESOURCES_MIX);
            set_WorkoutName(WORKOUT_NAMES);
            set_WorkoutDescriptions(WORKOUT_DESCRIPTIONS_MIX);
        }

        set_Max(max);
        create_WorkoutValues();
        create_BreakTimes();
    }
}
