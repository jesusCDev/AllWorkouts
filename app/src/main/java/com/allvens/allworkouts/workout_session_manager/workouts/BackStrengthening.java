package com.allvens.allworkouts.workout_session_manager.workouts;

import com.allvens.allworkouts.R;

public class BackStrengthening extends Workout{

    // lower means harder
    private final static double TRUNK_ROTATIONS_VALUE_DIFFICULTY = 0.6; // Trunk Rotations
    private final static double SIDE_PLANK_VALUE_DIFFICULTY = 0.4; // Side Plank (harder)
    private final static double LEG_RAISE_VALUE_DIFFICULTY = 0.5; // Straight Leg Raise
    private final static double HIP_ABDUCTION_VALUE_DIFFICULTY = 0.7; // Hip Abduction (easier)
    private final static double BRIDGES_VALUE_DIFFICULTY = 0.8; // Bridges (easiest)

    private final static String TRUNK_ROTATIONS_WORKOUT_NAME = "Standing Trunk Rotations with Resistance";
    private final static String SIDE_PLANK_WORKOUT_NAME = "Side Plank on Knees";
    private final static String LEG_RAISE_WORKOUT_NAME = "Straight Leg Raise";
    private final static String HIP_ABDUCTION_WORKOUT_NAME = "Side-Lying Hip Abduction";
    private final static String BRIDGES_WORKOUT_NAME = "Bridges";

    private final static String TRUNK_ROTATIONS_DES = "1. Stand with feet shoulder-width apart and knees slightly bent.\n" +
            "2. Hold a resistance band or light weight with both hands at chest level.\n" +
            "3. Keep your core engaged and hips facing forward throughout the movement.\n" +
            "4. Slowly rotate your torso to the right, keeping your arms straight.\n" +
            "5. Return to the center position with control.\n" +
            "6. Rotate to the left side in the same controlled manner.\n" +
            "7. Keep your lower body stable and avoid moving your hips.\n" +
            "8. Repeat this sequence for the number of reps required.\n" +
            "9. Remember: 1 rep = rotation to both sides (right and left).";

    private final static String SIDE_PLANK_DES = "1. Lie on your side with your knees bent at 90 degrees.\n" +
            "2. Place your forearm on the ground with your elbow directly under your shoulder.\n" +
            "3. Stack your knees on top of each other.\n" +
            "4. Engage your core and lift your hips off the ground.\n" +
            "5. Keep your body in a straight line from your head to your knees.\n" +
            "6. Hold this position for 10-15 seconds.\n" +
            "7. Lower back down with control.\n" +
            "8. Complete all reps on one side before switching sides.\n" +
            "9. Focus on keeping your core tight and avoiding sagging hips.";

    private final static String LEG_RAISE_DES = "1. Lie on your back with your legs straight and arms by your sides.\n" +
            "2. Press your lower back into the floor and engage your core.\n" +
            "3. Keep both legs straight and together.\n" +
            "4. Slowly lift both legs until they are perpendicular to the floor.\n" +
            "5. Pause briefly at the top position.\n" +
            "6. Slowly lower your legs back down without letting them touch the floor.\n" +
            "7. Keep your lower back pressed against the floor throughout.\n" +
            "8. If too difficult, bend your knees slightly.\n" +
            "9. Repeat for the number of reps required.";

    private final static String HIP_ABDUCTION_DES = "1. Lie on your side with your legs straight and stacked.\n" +
            "2. Rest your head on your lower arm and place your top hand on the floor for support.\n" +
            "3. Keep your core engaged and your body in a straight line.\n" +
            "4. Slowly lift your top leg up toward the ceiling.\n" +
            "5. Keep your leg straight and your foot flexed.\n" +
            "6. Lift only as high as comfortable while maintaining control.\n" +
            "7. Slowly lower your leg back to the starting position.\n" +
            "8. Avoid rolling forward or backward during the movement.\n" +
            "9. Complete all reps on one side before switching to the other side.";

    private final static String BRIDGES_DES = "1. Lie on your back with your knees bent and feet flat on the floor.\n" +
            "2. Place your arms by your sides with palms down for stability.\n" +
            "3. Keep your feet hip-width apart.\n" +
            "4. Engage your core and squeeze your glutes.\n" +
            "5. Press through your heels and lift your hips up toward the ceiling.\n" +
            "6. Create a straight line from your knees to your shoulders.\n" +
            "7. Hold briefly at the top position.\n" +
            "8. Slowly lower your hips back down to the starting position.\n" +
            "9. Repeat for the number of reps required, focusing on glute activation.";

    private final static String[] WORKOUT_DESCRIPTIONS_MIX = {TRUNK_ROTATIONS_DES, SIDE_PLANK_DES, LEG_RAISE_DES,
            HIP_ABDUCTION_DES, BRIDGES_DES};

    private final static double[] DIFFICULTY_VALUES = {TRUNK_ROTATIONS_VALUE_DIFFICULTY,
            SIDE_PLANK_VALUE_DIFFICULTY, LEG_RAISE_VALUE_DIFFICULTY, HIP_ABDUCTION_VALUE_DIFFICULTY,
            BRIDGES_VALUE_DIFFICULTY};

    private final static String[] WORKOUT_NAMES = {TRUNK_ROTATIONS_WORKOUT_NAME,
            SIDE_PLANK_WORKOUT_NAME, LEG_RAISE_WORKOUT_NAME, HIP_ABDUCTION_WORKOUT_NAME,
            BRIDGES_WORKOUT_NAME};

    private final static int[] IMAGE_RESOURCES_MIX = {R.drawable.standing_trunk_rotations_with_resistance,
            R.drawable.side_plan_on_knees, R.drawable.straight_leg_raise,
            R.drawable.side_lying_hip_abduction, R.drawable.bridges};

    public BackStrengthening(int type, int max){
        if(type == 0){
            set_DifficultyValues(WorkoutsSimpleValues.DIFFICULTY_VALUES);

            set_ImageResources(new int[] {R.drawable.bridges,
                    R.drawable.bridges, R.drawable.bridges,
                    R.drawable.bridges, R.drawable.bridges});

            set_WorkoutName(new String[]{BRIDGES_WORKOUT_NAME, BRIDGES_WORKOUT_NAME, BRIDGES_WORKOUT_NAME,
                    BRIDGES_WORKOUT_NAME, BRIDGES_WORKOUT_NAME});

            set_WorkoutDescriptions(new String[]{BRIDGES_DES, BRIDGES_DES, BRIDGES_DES, BRIDGES_DES,
                    BRIDGES_DES});
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