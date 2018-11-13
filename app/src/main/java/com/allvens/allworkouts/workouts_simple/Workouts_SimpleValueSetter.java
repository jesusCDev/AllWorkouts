package com.allvens.allworkouts.workouts_simple;

public class Workouts_SimpleValueSetter {

    private final static double FIRST_VALUE = 0.8;
    private final static double SECOND_VALUE = 0.6;
    private final static double THIRD_VALUE = 1.0;
    private final static double FORTH_VALUE = 0.4;
    private final static double FIFTH_VALUE = 0.9;

    private int max_reps;

    public void set_MaxReps(int max_reps){
        this.max_reps = max_reps;
        if(max_reps < 0) this.max_reps = 1;
    }

    public int get_FirstValue(){
        return (int) Math.ceil(max_reps * FIRST_VALUE);
    }

    public int get_SecondValue(){
        return (int) Math.ceil(max_reps * SECOND_VALUE);
    }

    public int get_ThirdValue(){
        return (int) Math.ceil(max_reps * THIRD_VALUE);
    }

    public int get_ForthValue(){
        return (int) Math.ceil(max_reps * FORTH_VALUE);
    }

    public int get_FifthValue(){
        return (int) Math.ceil(max_reps * FIFTH_VALUE);
    }
}
