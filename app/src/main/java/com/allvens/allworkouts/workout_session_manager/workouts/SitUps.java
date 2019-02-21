package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class SitUps extends Workout{

    // lower is harder
    private final static double PUNCHING_VALUE_DIFFICULTY = 0.5;
    private final static double NORMAL_VALUE_DIFFICULTY = 0.8;
    private final static double TO_KNEE_VALUE_DIFFICULTY = 0.4;
    private final static double BICYCLE_VALUE_DIFFICULTY = 0.6;
    private final static double CRUNCH_VALUE_DIFFICULTY = 0.3;

    private final static String PUNCHING_WORKOUT_NAME = "Punching Sit Up";
    private final static String NORMAL_WORKOUT_NAME = "Sit Up";
    private final static String TO_KNEE_WORKOUT_NAME = "To Knee Sit Up";
    private final static String BICYCLE_WORKOUT_NAME = "Bicycle";
    private final static String CRUNCH_WORKOUT_NAME = "Crunch";

    private final static String PUNCHING_DES =
            "1. Lay down with your legs bent in a V shape.\n" +
            "2. Make sure your feet are being held down to the floor.\n" +
            "3. Place both hands crunch to your chest.\n" +
            "4. Lift the top half of your body as to create a V shape with your body.\n" +
            "5. Swing one arm in a jabbing motion towards the opposite side of the arm.\n" +
            "6. Return arm to chest.\n" +
            "7. Return your top body half down to a laying position.\n" +
            "8. Repeat steps 4-7 while alternating arms as the number of reps required.";
    private final static String NORMAL_DES =
            "1. Lay down with your legs bent in a V shape.\n" +
            "2. Make sure your feet are being held down to the floor.\n" +
            "3. Cross both arms and place them on-top of the chest area\n" +
            "4. Lift the top half of your body as to create a V shape with your body.\n" +
            "5. Return your top body half down to a laying position.\n" +
            "6. Repeat steps 4-6 while alternating arms as the number of reps required.";
    private final static String TO_KNEE_DES =
            "1. Lay down with your legs bent in a V shape.\n" +
            "2. Make sure your feet are being held down to the floor.\n" +
            "3. Cross both arms and place them on-top of the chest area.\n" +
            "4. Lift the top half of your body as to create a V shape with your body.\n" +
            "5. While lifting your body up turn your body while trying to touch your knee with the elbow on the opposite side.\n" +
            "6. Twist your body back to facing forward.\n" +
            "7. Return your top body half down to a laying position.\n" +
            "8. Repeat steps 4-7 while alternating elbows to knees as the number of reps required.";
    private final static String BICYCLE_DES =
            "1. Lay down with your legs close and extended outward.\n" +
            "2. Hover both legs above ground.\n" +
            "3. Bend one leg while bring the knee as close to the top of the upper body.\n" +
            "4. Do not lift your top body off the ground while crunching your leg to your body.\n" +
            "5. Keep the other leg hovering above the ground.\n" +
            "6. Return the bent leg to its original position with the other leg.\n" +
            "7. Now repeat steps 3-6 with the opposite leg for one set.\n" +
            "8. Repeat steps 3-7 as the number of reps required.";
    private final static String CRUNCH_DES = "1. Lay down with your legs bent in a V shape.\n" +
            "2. Make sure your feet are being held down to the floor.\n" +
            "3. Place your hands behind your head while intertwining them together.\n" +
            "4. Lift the top half of your body as to create a V shape with your body.\n" +
            "5. Return your top body half down to a laying position.\n" +
            "6. Repeat steps 4-5 while alternating arms as the number of reps required.";

    private final static String[] WORKOUT_DESCRIPTIONS_MIX = {PUNCHING_DES, NORMAL_DES, TO_KNEE_DES,
            BICYCLE_DES, CRUNCH_DES};

    private final static double[] DIFFICULTY_VALUES = {PUNCHING_VALUE_DIFFICULTY,
            NORMAL_VALUE_DIFFICULTY, TO_KNEE_VALUE_DIFFICULTY, BICYCLE_VALUE_DIFFICULTY,
            CRUNCH_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {PUNCHING_WORKOUT_NAME,
            NORMAL_WORKOUT_NAME, TO_KNEE_WORKOUT_NAME, BICYCLE_WORKOUT_NAME,
            CRUNCH_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.ic_situp_punching,
            R.drawable.ic_situp,R.drawable.ic_situp_sidetoside,
            R.drawable.ic_situp_bicycle, R.drawable.ic_situp_crunch};

    public SitUps(int type, int max){
        if(type == 0){
            set_DifficultyValues(Workouts_SimpleValues.DIFFICULTY_VALUES);

            set_ImageResources(new int[] {R.drawable.ic_situp,
                    R.drawable.ic_situp,R.drawable.ic_situp,
                    R.drawable.ic_situp,R.drawable.ic_situp});

            set_WorkoutName(new String[]{NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME,
                    NORMAL_WORKOUT_NAME, NORMAL_WORKOUT_NAME});

            set_WorkoutDescriptions(new String[]{NORMAL_DES, NORMAL_DES, NORMAL_DES, NORMAL_DES,
                    NORMAL_DES});
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
