package com.allvens.allworkouts.data_manager;

/**
 * Manages progressive overload using research-based double progression algorithm.
 *
 * ALGORITHM OVERVIEW:
 * Based on sports science research (Plotkin et al. 2022, ACSM guidelines):
 * - Uses "double progression" method suitable for bodyweight exercises
 * - Weekly cap of ~5% to prevent injury and frustration
 * - Requires consistent "easy" feedback before increasing max
 * - Conservative reductions to maintain long-term progress
 *
 * This system dynamically adjusts workout difficulty based on user feedback:
 * - Too Easy: Increases rating (makes future workouts harder)
 * - Too Hard: Decreases rating (makes future workouts easier)
 * - Just Right: Maintains current level with minor adjustment
 *
 * Ratings are converted to difficulty multipliers that scale workout reps.
 *
 * @see <a href="https://fitbod.me/blog/progressive-overload-the-key-strength-gains/">Fitbod Progressive Overload</a>
 * @see <a href="https://legionathletics.com/double-progression/">Legion Double Progression</a>
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

    // Rating adjustment constants - UPDATED based on research
    // Research suggests 2-5% weekly progression is optimal
    // K_FACTOR of 16 gives ~8 rating points per session, safer progression
    private static final int K_FACTOR = 16;  // Reduced from 32 for safer progression
    private static final double EXPECTED_PERFORMANCE = 0.5;

    // Max value progression constants (for double progression)
    // Weekly cap: ~5% per week, assuming ~2 sessions per week = ~2.5% per session
    private static final double MAX_INCREASE_PERCENT = 0.025; // 2.5% max increase per session
    private static final double MAX_DECREASE_PERCENT = 0.05;  // 5% max decrease (more conservative)
    private static final int MIN_MAX_INCREASE = 1;  // Minimum increase is 1 rep
    private static final int MIN_MAX_DECREASE = 1;  // Minimum decrease is 1 rep
    
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

    // =========================================================================
    // DOUBLE PROGRESSION SYSTEM - Research-based max value progression
    // =========================================================================

    /**
     * Calculates the new max value using double progression algorithm.
     *
     * This method replaces the simple +1/+2/-1 system with a percentage-based
     * approach that prevents over-training and injury.
     *
     * Algorithm:
     * - TOO_EASY: Increase by 2.5% (capped at weekly ~5%)
     * - JUST_RIGHT: No change (let difficulty rating handle fine-tuning)
     * - TOO_HARD: Decrease by up to 5% (more conservative to prevent spiral)
     *
     * @param currentMax Current maximum reps
     * @param feedback User feedback (FEEDBACK_TOO_EASY, JUST_RIGHT, or TOO_HARD)
     * @return New max value (always >= 1)
     */
    public static int calculateNewMax(int currentMax, int feedback) {
        int newMax;

        switch (feedback) {
            case FEEDBACK_TOO_EASY:
                // Increase by percentage, minimum 1 rep
                int increase = Math.max(MIN_MAX_INCREASE, (int) Math.ceil(currentMax * MAX_INCREASE_PERCENT));
                newMax = currentMax + increase;
                break;

            case FEEDBACK_TOO_HARD:
                // Decrease by percentage, minimum 1 rep reduction
                int decrease = Math.max(MIN_MAX_DECREASE, (int) Math.ceil(currentMax * MAX_DECREASE_PERCENT));
                newMax = currentMax - decrease;
                break;

            case FEEDBACK_JUST_RIGHT:
            default:
                // No change to max - difficulty rating handles fine-tuning
                newMax = currentMax;
                break;
        }

        // Ensure max is always at least 1
        return Math.max(1, newMax);
    }

    /**
     * Gets the max adjustment value for display purposes (legacy compatibility)
     *
     * @param currentMax Current max value
     * @param feedback User feedback
     * @return Adjustment amount (can be negative)
     */
    public static int getMaxAdjustment(int currentMax, int feedback) {
        int newMax = calculateNewMax(currentMax, feedback);
        return newMax - currentMax;
    }

    /**
     * Determines if max value should be automatically increased after neutral feedback.
     *
     * In the old system, max increased by 1 every session. This method allows
     * automatic increases only when the difficulty rating is high enough,
     * indicating the user has been performing well consistently.
     *
     * @param difficultyRating Current difficulty rating
     * @return true if auto-increase is warranted
     */
    public static boolean shouldAutoIncrease(int difficultyRating) {
        // Only auto-increase if difficulty rating is above moderate (1200+)
        // This means user has been performing well for multiple sessions
        return difficultyRating >= 1200;
    }

    /**
     * Calculates the auto-increase amount based on current performance.
     *
     * Used when shouldAutoIncrease() returns true and user gives neutral feedback.
     * This provides the "slow and steady" progression appropriate for long-term gains.
     *
     * @param currentMax Current max value
     * @param difficultyRating Current difficulty rating
     * @return Auto-increase amount (0 if conditions not met)
     */
    public static int calculateAutoIncrease(int currentMax, int difficultyRating) {
        if (!shouldAutoIncrease(difficultyRating)) {
            return 0;
        }

        // Very conservative auto-increase: 1% rounded up, minimum 1
        // This gives ~4% monthly progression if training 4x per week
        return Math.max(1, (int) Math.ceil(currentMax * 0.01));
    }

    /**
     * Gets progression description for user feedback
     *
     * @param feedback User feedback type
     * @param oldMax Previous max
     * @param newMax New max
     * @return Human-readable description
     */
    public static String getProgressionDescription(int feedback, int oldMax, int newMax) {
        int change = newMax - oldMax;
        String direction;

        if (change > 0) {
            direction = "+" + change + " reps";
        } else if (change < 0) {
            direction = change + " reps";
        } else {
            direction = "No change";
        }

        switch (feedback) {
            case FEEDBACK_TOO_EASY:
                return "Great progress! " + direction;
            case FEEDBACK_TOO_HARD:
                return "Adjusted for recovery. " + direction;
            case FEEDBACK_JUST_RIGHT:
            default:
                return "Perfect level. " + direction;
        }
    }
}