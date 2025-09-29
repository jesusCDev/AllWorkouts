package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class PushUps extends Workout{

    // lower is harder
    private final static double BALANCE_VALUE_DIFFICULTY = 0.4; // Balance
    private final static double NORMAL_VALUE_DIFFICULTY = 0.7; // Normal
    private final static double TRIANGLE_VALUE_DIFFICULTY = 0.3; // Triangle
    private final static double SOLDIER_VALUE_DIFFICULTY = 0.6; // Soldier
    private final static double ONE_ARM_VALUE_DIFFICULTY = 0.2; // One Arm

    private final static String BALANCE_WORKOUT_NAME = "Balance Push Up";
    private final static String NORMAL_WORKOUT_NAME = "Push Up";
    private final static String TRIANGLE_WORKOUT_NAME = "Triangle Push Up";
    private final static String SOLIDER_WORKOUT_NAME = "Soldier Push Up";
    private final static String ONE_ARM_WORKOUT_NAME = "One Arm Push Up";

    private final static String BALANCE_DES = "1. Lay on the ground in a plank position with legs extend out straight.\n" +
            "2. Place both hands wider apart from your shoulders.\n" +
            "3. Balance your body on-top of hands and toes. Keep your arms bent a 90 degree angle.\n" +
            "4. Push off the ground by straightening your arms\n" +
            "5. Lift one arm off the ground and point it straight forward.\n" +
            "6. Lift the leg on the same side of the lifting arm off the ground at the same time.\n" +
            "7. Bring the leg and arm back down.\n" +
            "8. Make sure to keep your back straight and tighten your core.\n" +
            "9. Lower yourself back down by bending your arms again to a 90 degree angle.\n" +
            "10. Repeat steps 4-9 while alternating sides as the number of reps required.";
    private final static String NORMAL_DES = "1. Lay on the ground in a plank position with legs extend out straight.\n" +
            "2. Place both hands wider apart from your shoulders.\n" +
            "3. Balance your body on-top of hands and toes. Keep your arms bent a 90 degree angle.\n" +
            "4. Push off the ground by straightening your arms\n" +
            "5. Make sure to keep your back straight and tighten your core.\n" +
            "6. Lower yourself back down by bending your arms again to a 90 degree angle.\n" +
            "7. Repeat steps 4-6 as the number of reps required.";
    private final static String TRIANGLE_DES = "1. Lay on the ground in a plank position with legs extend out straight.\n" +
            "2. Place both hands in the center of your chest connecting the thumb to thumb and pointing finger to pointing finger creating a triangle.\n" +
            "3. Balance your body on-top of hands and toes.\n" +
            "4. Push off the ground by straightening your arms\n" +
            "5. Make sure to keep your back straight and tighten your core.\n" +
            "6. Lower yourself back down.\n" +
            "7. Repeat steps 4-6 as the number of reps required.";
    private final static String SOLIDER_DES = "1. Lay on the ground in a plank position with legs extend out straight.\n" +
            "2. Place both hands directly under their respected shoulders.\n" +
            "3. Balance your body on-top of hands and toes.\n" +
            "4. Push off the ground by straightening your arms\n" +
            "5. Keeping your elbows touching your body at all times.\n" +
            "6. Make sure to keep your back straight and tighten your core.\n" +
            "7. Lower yourself back down.\n" +
            "8. Keep your elbows touching your body on your way down.\n" +
            "9. Repeat steps 4-8 as the number of reps required.";
    private final static String ONE_ARM_DES = "1. Lay on the ground in a plank position with legs extend out straight.\n" +
            "2. Place one hand under the general chest area.\n" +
            "3. Rest your other hand on your back.\n" +
            "4. Spread your legs apart inorder to make balancing on one hand easier.\n" +
            "5. Balance your body on-top of hand and toes.\n" +
            "6. Push off the ground by straightening your arm\n" +
            "7. Make sure to keep your back straight and tighten your core.\n" +
            "8. Lower yourself back down.\n" +
            "9. Repeat steps 6-8 as the number of reps required.";

    private final static String[] WORKOUT_DESCRIPTIONS_MIX = {BALANCE_DES, NORMAL_DES, TRIANGLE_DES, SOLIDER_DES, ONE_ARM_DES};

    private final static double[] DIFFICULTY_VALUES = {BALANCE_VALUE_DIFFICULTY,
            NORMAL_VALUE_DIFFICULTY, TRIANGLE_VALUE_DIFFICULTY, SOLDIER_VALUE_DIFFICULTY,
            ONE_ARM_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {BALANCE_WORKOUT_NAME,
            NORMAL_WORKOUT_NAME, TRIANGLE_WORKOUT_NAME, SOLIDER_WORKOUT_NAME,
            ONE_ARM_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.ic_pushup_balance,
            R.drawable.ic_pushup,R.drawable.ic_pushup_triangle,
            R.drawable.ic_pushup_soldier, R.drawable.ic_pushup_onearm};

    public PushUps(int type, int max){
        if(type == 0){
            set_DifficultyValues(WorkoutsSimpleValues.DIFFICULTY_VALUES);

            set_ImageResources(new int[] {R.drawable.ic_pushup,
                    R.drawable.ic_pushup,R.drawable.ic_pushup,
                    R.drawable.ic_pushup,R.drawable.ic_pushup});

            set_WorkoutName(new String[]{NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME,
                    NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME});

            set_WorkoutDescriptions(new String[]{NORMAL_DES, NORMAL_DES, NORMAL_DES, NORMAL_DES, NORMAL_DES});
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
