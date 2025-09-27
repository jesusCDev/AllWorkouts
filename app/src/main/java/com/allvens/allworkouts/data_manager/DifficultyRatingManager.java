package com.allvens.allworkouts.data_manager;

/**
 * Manages Elo-like difficulty rating system for workouts.
 * 
 * This system dynamically adjusts workout difficulty based on user feedback:
 * - Too Easy: Increases rating (makes future workouts harder)
 * - Too Hard: Decreases rating (makes future workouts easier)  
 * - Just Right: Small adjustment to maintain current level
 * 
 * Ratings are converted to difficulty multipliers that scale workout reps.
 */
public class DifficultyRatingManager {
    
    // Rating system constants
    public static final int DEFAULT_RATING = 1000;
    public static final int MIN_RATING = 400;
    public static final int MAX_RATING = 2000;
    
    // Feedback types
    public static final int FEEDBACK_TOO_EASY = 1;
    public static final int FEEDBACK_JUST_RIGHT = 0;
    public static final int FEEDBACK_TOO_HARD = -1;
    
    // Rating adjustment constants
    private static final int K_FACTOR = 32;  // Maximum rating change per feedback
    private static final double EXPECTED_PERFORMANCE = 0.5; // Expected success rate
    
    /**
     * Calculates new rating based on user feedback using Elo-like algorithm
     * 
     * @param currentRating Current difficulty rating
     * @param feedback User feedback (TOO_EASY, JUST_RIGHT, or TOO_HARD)
     * @return New adjusted rating
     */
    public static int calculateNewRating(int currentRating, int feedback) {
        // Convert feedback to actual performance (0.0 to 1.0)
        double actualPerformance = feedbackToPerformance(feedback);
        
        // Calculate rating adjustment using simplified Elo formula
        double ratingChange = K_FACTOR * (actualPerformance - EXPECTED_PERFORMANCE);
        
        int newRating = currentRating + (int) Math.round(ratingChange);
        
        // Clamp rating within bounds
        return Math.max(MIN_RATING, Math.min(MAX_RATING, newRating));
    }
    
    /**
     * Converts user feedback to performance score
     * 
     * @param feedback User feedback constant
     * @return Performance score (0.0 = too hard, 0.5 = just right, 1.0 = too easy)
     */
    private static double feedbackToPerformance(int feedback) {
        switch (feedback) {
            case FEEDBACK_TOO_EASY:
                return 1.0; // Perfect performance, workout was too easy
            case FEEDBACK_JUST_RIGHT:
                return 0.5; // Expected performance, workout was appropriate
            case FEEDBACK_TOO_HARD:
                return 0.0; // Failed performance, workout was too hard
            default:
                return 0.5; // Default to neutral
        }
    }
    
    /**
     * Converts difficulty rating to multiplier for workout calculations.
     * Higher rating = higher multiplier = more reps = harder workout
     * 
     * @param rating Difficulty rating (400-2000)
     * @return Difficulty multiplier (0.3-1.2 range)
     */
    public static double ratingToMultiplier(int rating) {
        // Clamp rating to valid range
        rating = Math.max(MIN_RATING, Math.min(MAX_RATING, rating));
        
        // Convert rating to multiplier using linear interpolation
        // Rating 400 -> multiplier 0.3 (easier)
        // Rating 1000 -> multiplier 0.7 (baseline)  
        // Rating 2000 -> multiplier 1.2 (harder)
        
        double normalizedRating = (rating - MIN_RATING) / (double) (MAX_RATING - MIN_RATING);
        return 0.3 + (normalizedRating * 0.9); // Maps 0-1 to 0.3-1.2
    }
    
    /**
     * Gets the baseline difficulty multiplier for new workouts
     * 
     * @return Baseline multiplier (0.7)
     */
    public static double getBaselineMultiplier() {
        return ratingToMultiplier(DEFAULT_RATING);
    }
    
    /**
     * Calculates workout reps using dynamic difficulty rating
     * 
     * @param maxReps User's maximum reps capability
     * @param rating Current difficulty rating
     * @return Calculated reps for this workout session
     */
    public static int calculateWorkoutReps(int maxReps, int rating) {
        double multiplier = ratingToMultiplier(rating);
        return (int) Math.ceil(maxReps * multiplier);
    }
    
    /**
     * Gets a human-readable description of the current difficulty level
     * 
     * @param rating Current difficulty rating
     * @return Description string
     */
    public static String getDifficultyDescription(int rating) {
        if (rating < 600) {
            return "Very Easy";
        } else if (rating < 800) {
            return "Easy";
        } else if (rating < 1200) {
            return "Moderate";
        } else if (rating < 1600) {
            return "Hard";
        } else {
            return "Very Hard";
        }
    }
    
    /**
     * Debug method to log rating changes (useful for development)
     * 
     * @param workoutName Name of the workout
     * @param oldRating Previous rating
     * @param newRating New rating after feedback
     * @param feedback User feedback that caused the change
     */
    public static void logRatingChange(String workoutName, int oldRating, int newRating, int feedback) {
        String feedbackStr;
        switch (feedback) {
            case FEEDBACK_TOO_EASY:
                feedbackStr = "TOO_EASY";
                break;
            case FEEDBACK_JUST_RIGHT:
                feedbackStr = "JUST_RIGHT";
                break;
            case FEEDBACK_TOO_HARD:
                feedbackStr = "TOO_HARD";
                break;
            default:
                feedbackStr = "UNKNOWN";
        }
        
        System.out.println(String.format(
            "[DifficultyRating] %s: %d -> %d (%+d) [%s] Multiplier: %.2f -> %.2f",
            workoutName, oldRating, newRating, (newRating - oldRating), feedbackStr,
            ratingToMultiplier(oldRating), ratingToMultiplier(newRating)
        ));
    }
}