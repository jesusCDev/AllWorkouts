package com.allvens.allworkouts.workout_session_manager.workouts;

public class Workout {

    private int[] workoutValues;
    private String[] workoutNames;
    private String[] workoutsDescription;
    private int[] imageResources;
    private double[] difficultyValues;
    private int[] workoutBreakTime;
    private int max;

    public void create_WorkoutValues(){
        workoutValues = new int[5];
        for(int i = 0; i < 5; i++){
            workoutValues[i] = (int)(Math.ceil(max * difficultyValues[i]));
        }
    }

    public void create_BreakTimes(){
        workoutBreakTime = new int[4];
        for(int i = 0; i < 4; i++){
            workoutBreakTime[i] = (10000 * (int)(Math.round(10 * difficultyValues[i])));
        }
    }

    public int get_WorkoutValue(int pos){
        return workoutValues[pos];
    }

    public String get_WorkoutName(int pos){
        return workoutNames[pos];
    }

    public String get_WorkoutDescription(int pos){
        return workoutsDescription[pos];
    }

    public int get_WorkoutImage(int pos){
        return imageResources[pos];
    }

    public void set_Max(int max){
        this.max = max;
    }

    public void set_WorkoutName(String[] workoutName){
        this.workoutNames = workoutName;
    }

    public void set_WorkoutDescriptions(String[] workoutsDescription){
        this.workoutsDescription = workoutsDescription;
    }

    public void set_ImageResources(int[] imageResources){
        this.imageResources = imageResources;
    }

    public void set_DifficultyValues(double[] difficultyValues) {
        this.difficultyValues = difficultyValues;
    }

    public int get_BreakTime(int pos) {
        return workoutBreakTime[(pos - 1)];
    }
}
