package com.allvens.allworkouts.data_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class WorkoutWrapper {

    private SQLiteDatabase database;
    private WorkoutSQLiteOpenHelper dbHelper;

    public WorkoutWrapper(Context context){
        this.dbHelper = new WorkoutSQLiteOpenHelper(context);
    }

    public void open(){
        this.database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void createWorkout(WorkoutInfo workout){
        ContentValues values = new ContentValues();

        values.put(WorkoutContract.Workout_Entry.COLUMN_WORKOUT, workout.getWorkout());
        values.put(WorkoutContract.Workout_Entry.COLUMN_TYPE, workout.getType());
        values.put(WorkoutContract.Workout_Entry.COLUMN_PROGRESS, workout.getProgress());
        values.put(WorkoutContract.Workout_Entry.COLUMN_MAX, workout.getMax());
        values.put(WorkoutContract.Workout_Entry.COLUMN_DIFFICULTY_RATING, workout.getDifficultyRating());

        List<WorkoutHistoryInfo> workouts = workout.getHistory();
        long rowID                         = database.insert(
                WorkoutContract.Workout_Entry.TABLE_NAME,
                null,
                values
        );

        if(workouts != null && !workouts.isEmpty()){
            for(WorkoutHistoryInfo history: workouts){
                createWorkoutHistory(history, rowID);
            }
        }
    }

    /********** Getter Methods **********/

    public WorkoutInfo getWorkout(String chosenWorkout) {
        for(WorkoutInfo workout: getAllWorkouts()){
            if(workout.getWorkout().equalsIgnoreCase(chosenWorkout)){
                return workout;
            }
        }

        return null;
    }

    public List<WorkoutInfo> getAllWorkouts(){
        List<WorkoutInfo> workouts = new ArrayList<>();
        String selectQuery          = "SELECT * FROM workout_info";
        Cursor cursor               = database.rawQuery(selectQuery, null);

        try{
            while(cursor.moveToNext()){
                // Get difficulty rating, defaulting to 1000 if column doesn't exist (migration scenario)
                int difficultyRating = 1000;
                int difficultyColumnIndex = cursor.getColumnIndex(WorkoutContract.Workout_Entry.COLUMN_DIFFICULTY_RATING);
                if (difficultyColumnIndex != -1) {
                    difficultyRating = cursor.getInt(difficultyColumnIndex);
                }
                
                WorkoutInfo workout = new WorkoutInfo(
                        getStringSafely(cursor, WorkoutContract.Workout_Entry.COLUMN_WORKOUT),
                        getIntSafely(cursor, WorkoutContract.Workout_Entry.COLUMN_MAX),
                        getIntSafely(cursor, WorkoutContract.Workout_Entry.COLUMN_TYPE),
                        getIntSafely(cursor, WorkoutContract.Workout_Entry.COLUMN_PROGRESS),
                        difficultyRating);

                workout.setId(getIntSafely(cursor, WorkoutContract.Workout_Entry._ID));
                workouts.add(workout);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return workouts;
    }

    public void updateWorkout(WorkoutInfo workout){
        ContentValues values = new ContentValues();

        values.put(WorkoutContract.Workout_Entry.COLUMN_WORKOUT, workout.getWorkout());
        values.put(WorkoutContract.Workout_Entry.COLUMN_MAX, workout.getMax());
        values.put(WorkoutContract.Workout_Entry.COLUMN_TYPE, workout.getType());
        values.put(WorkoutContract.Workout_Entry.COLUMN_PROGRESS, workout.getProgress());
        values.put(WorkoutContract.Workout_Entry.COLUMN_DIFFICULTY_RATING, workout.getDifficultyRating());

        String selection       = WorkoutContract.Workout_Entry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(workout.getId())};

        database.update(WorkoutContract.Workout_Entry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void deleteWorkout(WorkoutInfo workout){
        deleteWorkoutHistory(workout.getId());

        String selection       = WorkoutContract.Workout_Entry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(workout.getId())};

        database.delete(WorkoutContract.Workout_Entry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAllWorkouts(){
        database.delete(WorkoutContract.Workout_Entry.TABLE_NAME, null, null);
    }

    public void createWorkoutHistory(WorkoutHistoryInfo workoutHistory, long workoutID){

        ContentValues values = new ContentValues();

        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_WORKOUT_ID, workoutID);
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_FIRST, workoutHistory.getFirst_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_SECOND, workoutHistory.getSecond_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_THIRD, workoutHistory.getThird_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_FORTH, workoutHistory.getForth_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_FIFTH, workoutHistory.getFifth_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_MAX, workoutHistory.getMax_value());
        values.put(WorkoutContract.WorkoutHistory_Entry.COLUMN_COMPLETION_DATE, workoutHistory.getCompletionDate());
        database.insert(WorkoutContract.WorkoutHistory_Entry.TABLE_NAME, null, values);
    }

    public List<WorkoutHistoryInfo> getHistoryForWorkout(long workoutID){
        List<WorkoutHistoryInfo> workouts = new ArrayList<>();
        String selectQuery                 = "SELECT * FROM workout_history WHERE id = " + workoutID;
        Cursor cursor                      = database.rawQuery(selectQuery, null);

        try{
            while(cursor.moveToNext()){
                // Get completion date, defaulting to 0 if column doesn't exist (migration scenario)
                long completionDate = 0;
                int dateColumnIndex = cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_COMPLETION_DATE);
                if (dateColumnIndex != -1) {
                    completionDate = cursor.getLong(dateColumnIndex);
                }
                
                WorkoutHistoryInfo workoutHistory_info = new WorkoutHistoryInfo(
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FIRST),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_SECOND),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_THIRD),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FORTH),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FIFTH),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_MAX),
                        completionDate);

                workoutHistory_info.setId(getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry._ID));
                workouts.add(workoutHistory_info);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }

        return workouts;
    }

    private void deleteWorkoutHistory(long id){
        String selection       = WorkoutContract.WorkoutHistory_Entry.COLUMN_WORKOUT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id)};

        database.delete(WorkoutContract.WorkoutHistory_Entry.TABLE_NAME, selection, selectionArgs);
    }

    public void deleteAllHistoryWorkouts(){
        database.delete(WorkoutContract.WorkoutHistory_Entry.TABLE_NAME, null, null);
    }
    
    /**
     * Get workout history for a specific date range
     * @param startDate Unix timestamp (seconds) for range start
     * @param endDate Unix timestamp (seconds) for range end
     * @return List of workout history entries in the date range
     */
    public List<WorkoutHistoryInfo> getWorkoutHistoryForDateRange(long startDate, long endDate) {
        List<WorkoutHistoryInfo> workouts = new ArrayList<>();
        String selectQuery = "SELECT * FROM workout_history WHERE completion_date >= ? AND completion_date <= ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(startDate), String.valueOf(endDate)});
        
        try {
            while(cursor.moveToNext()) {
                long completionDate = 0;
                int dateColumnIndex = cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_COMPLETION_DATE);
                if (dateColumnIndex != -1) {
                    completionDate = cursor.getLong(dateColumnIndex);
                }
                
                WorkoutHistoryInfo workoutHistory_info = new WorkoutHistoryInfo(
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FIRST),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_SECOND),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_THIRD),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FORTH),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_FIFTH),
                        getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry.COLUMN_MAX),
                        completionDate);
                
                workoutHistory_info.setId(getIntSafely(cursor, WorkoutContract.WorkoutHistory_Entry._ID));
                workouts.add(workoutHistory_info);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        
        return workouts;
    }
    
    /**
     * Get all workout history entries for a specific day
     * @param dayStartTimestamp Unix timestamp (seconds) for start of day (00:00:00)
     * @param dayEndTimestamp Unix timestamp (seconds) for end of day (23:59:59)
     * @return Count of workouts completed on that day
     */
    public int getWorkoutCountForDay(long dayStartTimestamp, long dayEndTimestamp) {
        return getWorkoutHistoryForDateRange(dayStartTimestamp, dayEndTimestamp).size();
    }
    
    /**
     * Get list of unique workout IDs that were completed on a specific day
     * @param dayStartTimestamp Unix timestamp (seconds) for start of day (00:00:00)
     * @param dayEndTimestamp Unix timestamp (seconds) for end of day (23:59:59)
     * @return List of unique workout IDs completed that day
     */
    public java.util.Set<Long> getUniqueWorkoutIdsForDay(long dayStartTimestamp, long dayEndTimestamp) {
        java.util.Set<Long> uniqueWorkoutIds = new java.util.HashSet<>();
        String selectQuery = "SELECT DISTINCT id FROM workout_history WHERE completion_date >= ? AND completion_date <= ?";
        Cursor cursor = database.rawQuery(selectQuery, new String[]{String.valueOf(dayStartTimestamp), String.valueOf(dayEndTimestamp)});
        
        try {
            int idColumnIndex = cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_WORKOUT_ID);
            if (idColumnIndex != -1) {
                while(cursor.moveToNext()) {
                    uniqueWorkoutIds.add(cursor.getLong(idColumnIndex));
                }
            }
        } finally {
            if(cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        
        return uniqueWorkoutIds;
    }
    
    /**
     * Safely get string value from cursor, checking if column exists
     */
    private String getStringSafely(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getString(columnIndex);
        }
        return ""; // or appropriate default value
    }
    
    /**
     * Safely get int value from cursor, checking if column exists
     */
    private int getIntSafely(Cursor cursor, String columnName) {
        int columnIndex = cursor.getColumnIndex(columnName);
        if (columnIndex != -1) {
            return cursor.getInt(columnIndex);
        }
        return 0; // or appropriate default value
    }
}
