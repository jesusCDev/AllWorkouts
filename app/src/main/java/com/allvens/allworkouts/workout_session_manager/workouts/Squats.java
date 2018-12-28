package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class Squats extends Workout{

    // lower means harder
    private final static double PUNCHING_VALUE_DIFFICULTY = 0.6;
    private final static double SQUAT_VALUE_DIFFICULTY = 0.8;
    private final static double KICKING_VALUE_DIFFICULTY = 0.6;
    private final static double PILE_VALUE_DIFFICULTY = 0.7;
    private final static double JUMPING_VALUE_DIFFICULTY = 0.4;

    private final static String PUNCHING_WORKOUT_NAME = "Punching Squat";
    private final static String SQUAT_WORKOUT_NAME = "Squat";
    private final static String KICKING_WORKOUT_NAME = "Kicking Squat";
    private final static String PILE_WORKOUT_NAME = "Pile Squat";
    private final static String JUMPING_WORKOUT_NAME = "Jumping Squat";

    private final static String PUNCHING_DES = "1. Stand straight with feet a little wider then their respected shoulder apart.\n" +
            "2. Pull elbows close to body and hands clench near your chest.\n" +
            "3. Lower yourself down by bending your legs.\n" +
            "4. Keep your back straight or else you will feel pain on your lower back.\n" +
            "5. Make sure your chest stays up straight.\n" +
            "6. On your descend down your butt will stick out.\n" +
            "7. Lower yourself enough for you bent knees to make a 90 degree angle.\n" +
            "8. Most of your weight should be on the heels of your feet.\n" +
            "9. Lift yourself up(keep steps 4-5 in mind) by straightening out your legs.\n" +
            "10. On your descend up start slowly twisting towards either left or right almost 30 degrees.\n" +
            "11. Once you've reached the top, shoot a quick jab with the arm opposite to the direction you twisted towards.\n" +
            "12. Twist back to your original position once you are done with your jab.\n" +
            "13. Repeat steps 3-12 while alternating arms as the number of sets required.";
    private final static String SQUAT_DES = "1. Stand straight with feet a little wider then their respected shoulder apart.\n" +
            "2. Stretch hands out in front as much as needed to make balancing easier.\n" +
            "3. Lower yourself down by bending your legs.\n" +
            "4. Keep your back straight or else you will feel pain on your lower back.\n" +
            "5. Make sure your chest stays up straight.\n" +
            "6. On your descend down your butt will stick out.\n" +
            "7. Lower yourself enough for you bent knees to make a 90 degree angle.\n" +
            "8. Most of your weight should be on the heels of your feet.\n" +
            "9. Lift yourself up(keep steps 4-5 in mind) by straightening out your legs.\n" +
            "10. Repeat steps 3-9 as the number of sets required.";
    private final static String KICKING_DES = "1. Stand straight with feet a little wider then their respected shoulder apart.\n" +
            "2. Stretch hands out in front as much as needed to make balancing easier.\n" +
            "3. Lower yourself down by bending your legs.\n" +
            "4. Keep your back straight or else you will feel pain on your lower back.\n" +
            "5. Make sure your chest stays up straight.\n" +
            "6. On your descend down your butt will stick out.\n" +
            "7. Lower yourself enough for you bent knees to make a 90 degree angle.\n" +
            "8. Most of your weight should be on the heels of your feet.\n" +
            "9. Lift yourself up(keep steps 4-5 in mind) by straightening out your legs.\n" +
            "10. Once you've reached the top lift one leg up while balancing on the other.\n" +
            "11. Swing the lifted leg upward till the toe almost reaches your hips height.\n" +
            "12. Return the leg back to its original position.\n" +
            "13. Repeat steps 3-12 as the number of sets required.";
    private final static String PILE_SQUAT = "1. Stand straight with feet wider then their respected shoulder apart.\n" +
            "2. Point each foot outward by 45 degrees.\n" +
            "3. Stretch hands out in front as much as needed to make balancing easier.\n" +
            "4. Lower yourself down by bending your legs.\n" +
            "5. Keep your back straight or else you will feel pain on your lower back.\n" +
            "6. Make sure your chest stays up straight.\n" +
            "7. Lower yourself enough for you bent knees to make a 90 degree angle.\n" +
            "8. Most of your weight should be distributed evenly throughout your foot.\n" +
            "9. Lift yourself up(keep steps 4-5 in mind) by straightening out your legs.\n" +
            "10. Repeat steps 4-9 as the number of sets required.";
    private final static String JUMPING_DES = "1. Stand straight with feet a little wider then their respected shoulder apart.\n" +
            "2. Clench your elbows together and bring your hands close to your chest while touching your fingers tip to tip.\n" +
            "3. Lower yourself down by bending your legs.\n" +
            "4. Keep your back straight or else you will feel pain on your lower back.\n" +
            "5. Make sure your chest stays up straight.\n" +
            "6. On your descend down your butt will stick out.\n" +
            "7. Lower yourself enough for you bent knees to make a 90 degree angle.\n" +
            "8. Most of your weight should be on the heels of your feet.\n" +
            "9. Push yourself up(keep steps 4-5 in mind) by straightening out your legs with enough power to lift you off the ground.\n" +
            "10. Stretch your arms downward.\n" +
            "11. During the fall return your arms back to their original position\n" +
            "12. Bend your legs to soften the impact and land on your whole foot to evenly distribute the weight.\n" +
            "13. Make sure knees are behind your toes.\n" +
            "14. Repeat steps 3-13 as the number of sets required.";

    private final static String[] WORKOUT_DESCRIPTIONS_MIX = {PUNCHING_DES, SQUAT_DES, KICKING_DES,
            PILE_SQUAT, JUMPING_DES};

    private final static double[] DIFFICULTY_VALUES = {PUNCHING_VALUE_DIFFICULTY,
            SQUAT_VALUE_DIFFICULTY, KICKING_VALUE_DIFFICULTY, PILE_VALUE_DIFFICULTY,
            JUMPING_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {PUNCHING_WORKOUT_NAME,
            SQUAT_WORKOUT_NAME, KICKING_WORKOUT_NAME, PILE_WORKOUT_NAME,
            JUMPING_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.ic_squat_punching,
            R.drawable.ic_squat,R.drawable.ic_squat_kicking,
            R.drawable.ic_squat_pile, R.drawable.ic_squat_jump};

    public Squats(int type, int max){
        if(type == 0){
            set_DifficultyValues(Workouts_SimpleValues.DIFFICULTY_VALUES);

            set_ImageResources(new int[] {R.drawable.ic_squat,
                    R.drawable.ic_squat,R.drawable.ic_squat,
                    R.drawable.ic_squat,R.drawable.ic_squat});

            set_WorkoutName(new String[]{SQUAT_WORKOUT_NAME, SQUAT_WORKOUT_NAME, SQUAT_WORKOUT_NAME,
                    SQUAT_WORKOUT_NAME, SQUAT_WORKOUT_NAME});

            set_WorkoutDescriptions(new String[] {SQUAT_DES, SQUAT_DES, SQUAT_DES, SQUAT_DES, SQUAT_DES});
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
