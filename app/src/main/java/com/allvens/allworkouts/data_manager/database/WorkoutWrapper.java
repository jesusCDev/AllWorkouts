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
                        cursor.getString(cursor.getColumnIndex(WorkoutContract.Workout_Entry.COLUMN_WORKOUT)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.Workout_Entry.COLUMN_MAX)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.Workout_Entry.COLUMN_TYPE)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.Workout_Entry.COLUMN_PROGRESS)),
                        difficultyRating);

                workout.setId(cursor.getInt(cursor.getColumnIndex(WorkoutContract.Workout_Entry._ID)));
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
        database.insert(WorkoutContract.WorkoutHistory_Entry.TABLE_NAME, null, values);
    }

    public List<WorkoutHistoryInfo> getHistoryForWorkout(long workoutID){
        List<WorkoutHistoryInfo> workouts = new ArrayList<>();
        String selectQuery                 = "SELECT * FROM workout_history WHERE id = " + workoutID;
        Cursor cursor                      = database.rawQuery(selectQuery, null);

        try{
            while(cursor.moveToNext()){
                WorkoutHistoryInfo workoutHistory_info = new WorkoutHistoryInfo(
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_FIRST)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_SECOND)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_THIRD)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_FORTH)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_FIFTH)),
                        cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry.COLUMN_MAX)));

                workoutHistory_info.setId(cursor.getInt(cursor.getColumnIndex(WorkoutContract.WorkoutHistory_Entry._ID)));
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
}
