package com.allvens.allworkouts.workouts_mix;

public class Workouts_MixValueSetter {

    private int FIRST_VALUE_DIFFICULTY;
    private int SECOND_VALUE_DIFFICULTY;
    private int THIRD_VALUE_DIFFICULTY;
    private int FORTH_VALUE_DIFFICULTY;
    private int FIFTH_VALUE_DIFFICULTY;

    private int max_reps;

    public void set_MaxReps(int max_reps){
        this.max_reps = max_reps;
        if(max_reps < 0) this.max_reps = 1;
    }

    public void set_DifficultValues(int[] difficultValues){
        FIRST_VALUE_DIFFICULTY = difficultValues[0];
        SECOND_VALUE_DIFFICULTY = difficultValues[1];
        THIRD_VALUE_DIFFICULTY = difficultValues[2];
        FORTH_VALUE_DIFFICULTY = difficultValues[3];
        FIFTH_VALUE_DIFFICULTY = difficultValues[4];
    }

    public int get_FirstValue(){
        return (int) Math.ceil(max_reps/FIRST_VALUE_DIFFICULTY);
    }

    public int get_SecondValue(){
        return (int) Math.ceil(max_reps/SECOND_VALUE_DIFFICULTY);
    }

    public int get_ThirdValue(){
        return (int) Math.ceil(max_reps/THIRD_VALUE_DIFFICULTY);
    }

    public int get_ForthValue(){
        return (int) Math.ceil(max_reps/FORTH_VALUE_DIFFICULTY);
    }

    public int get_FifthValue(){
        return (int) Math.ceil(max_reps/FIFTH_VALUE_DIFFICULTY);
    }
}
