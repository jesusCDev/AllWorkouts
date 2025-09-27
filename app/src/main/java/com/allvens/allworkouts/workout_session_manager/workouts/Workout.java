package com.allvens.allworkouts.workout_session_manager.workouts;

public class Workout {

    private int[] workoutValues;
    private String[] workoutNames;
    private String[] workoutsDescription;
    private int[] imageResources;
    private double[] difficultyValues;
    private int[] workoutBreakTime;
    private int max;
    private int difficultyRating; // Dynamic difficulty rating from database

    public void create_WorkoutValues(){
        workoutValues = new int[5];
        for(int i = 0; i < 5; i++){
            // Use dynamic difficulty rating if available, otherwise fall back to static values
            double multiplier = (difficultyRating > 0) ? 
                getDynamicDifficultyMultiplier(difficultyValues[i]) : 
                difficultyValues[i];
            workoutValues[i] = (int)(Math.ceil(max * multiplier));
        }
    }
    
    /**
     * Calculates dynamic difficulty multiplier based on rating and base difficulty
     * Combines the user's personal rating with the exercise's base difficulty
     */
    private double getDynamicDifficultyMultiplier(double baseDifficulty) {
        // Import the rating calculation here to avoid circular dependencies
        // We'll implement this inline to keep it simple
        
        // Clamp rating to valid range (400-2000)
        int clampedRating = Math.max(400, Math.min(2000, difficultyRating));
        
        // Convert rating to a multiplier (400->0.3, 1000->0.7, 2000->1.2)
        double normalizedRating = (clampedRating - 400) / (double) (2000 - 400);
        double ratingMultiplier = 0.3 + (normalizedRating * 0.9);
        
        // Blend the rating multiplier with the base difficulty
        // This preserves the relative difficulty between different sets within a workout
        // while adjusting the overall difficulty based on user rating
        double blendFactor = 0.7; // How much to weight the rating vs base difficulty
        return (ratingMultiplier * blendFactor) + (baseDifficulty * (1 - blendFactor));
    }

    public void create_BreakTimes(){
        workoutBreakTime = new int[4];
        for(int i = 0; i < 4; i++){
            workoutBreakTime[i] = (10000 * (int)(Math.round(10 * difficultyValues[i])));
        }
    }

    public int getWorkoutValue(int pos){
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
    
    public void setDifficultyRating(int difficultyRating) {
        this.difficultyRating = difficultyRating;
    }
    
    public int getDifficultyRating() {
        return difficultyRating;
    }

    public int get_BreakTime(int pos) {
        return workoutBreakTime[(pos - 1)];
    }
}
