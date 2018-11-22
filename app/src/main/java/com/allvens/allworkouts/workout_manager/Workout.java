package com.allvens.allworkouts.workout_manager;

public class Workout {

    private int[] workoutValues;
    private double[] difficultyValues;
    private String[] imageResources;
    private int max;

    public void create_WorkoutValues(){
        for(int i = 0; i < 5; i++){
            workoutValues[i] = (int)(Math.round(max * difficultyValues[i]));
        }
    }

    public void set_Max(int max){
        this.max = max;
    }

    public void set_ImageResources(String[] imageResources){
        this.imageResources = imageResources;
    }

    public void set_DifficultyValues(double[] difficultyValues) {
        this.difficultyValues = difficultyValues;
    }
}
