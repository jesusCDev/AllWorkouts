package com.allvens.allworkouts.data_manager.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public class Workout_Wrapper {

    private SQLiteDatabase database;
    private Workout_SQLiteOpenHelper dbHelper;

    public Workout_Wrapper(Context context){
        this.dbHelper = new Workout_SQLiteOpenHelper(context);
    }

    public void open(){
        this.database = dbHelper.getWritableDatabase();
    }

    public void close(){
        dbHelper.close();
    }

    public void create_Workout(Workout_Info workout){
        ContentValues values = new ContentValues();
        values.put(Workout_Contract.Workout_Entry.COLUMN_WORKOUT, workout.getWorkout());
        values.put(Workout_Contract.Workout_Entry.COLUMN_TYPE, workout.getType());
        values.put(Workout_Contract.Workout_Entry.COLUMN_PROGRESS, workout.getProgress());
        values.put(Workout_Contract.Workout_Entry.COLUMN_MAX, workout.getMax());

        long rowID = database.insert(Workout_Contract.Workout_Entry.TABLE_NAME, null, values);

        List<WorkoutHistory_Info> workouts = workout.getHistory();
        if(workouts != null && workouts.size() > 0){
            for(WorkoutHistory_Info history: workouts){
                create_WorkoutHistory(history, rowID);
            }
        }
    }

    public void create_WorkoutHistory(WorkoutHistory_Info workoutHistory, long workoutID){

        ContentValues values = new ContentValues();

        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_WORKOUT_ID, workoutID);

        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_FIRST, workoutHistory.getFirst_value());
        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_SECOND, workoutHistory.getSecond_value());
        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_THIRD, workoutHistory.getThird_value());
        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_FORTH, workoutHistory.getForth_value());
        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_FIFTH, workoutHistory.getFifth_value());
        values.put(Workout_Contract.WorkoutHistory_Entry.COLUMN_MAX, workoutHistory.getMax_value());

        long rowID = database.insert(Workout_Contract.WorkoutHistory_Entry.TABLE_NAME, null, values);
    }

    public List<Workout_Info> get_AllWorkouts(){
        List<Workout_Info> workouts = new ArrayList<>();

        String selectQuery = "SELECT * FROM workout_info";
        Cursor cursor = database.rawQuery(selectQuery, null);
        try{
            while(cursor.moveToNext()){
                Workout_Info workout = new Workout_Info(
                        cursor.getString(cursor.getColumnIndex(Workout_Contract.Workout_Entry.COLUMN_WORKOUT)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.Workout_Entry.COLUMN_MAX)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.Workout_Entry.COLUMN_TYPE)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.Workout_Entry.COLUMN_PROGRESS)));

                workout.setId(cursor.getInt(cursor.getColumnIndex(Workout_Contract.Workout_Entry._ID)));
                workouts.add(workout);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return workouts;
    }

    public List<WorkoutHistory_Info> get_HistoryForWorkout(long workoutID){
        List<WorkoutHistory_Info> workouts = new ArrayList<>();

        String selectQuery = "SELECT * FROM workout_history WHERE id = " + workoutID;
        Cursor cursor = database.rawQuery(selectQuery, null);
        try{
            while(cursor.moveToNext()){
                WorkoutHistory_Info workoutHistory_info = new WorkoutHistory_Info(
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_FIRST)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_SECOND)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_THIRD)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_FORTH)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_FIFTH)),
                        cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry.COLUMN_MAX)));

                workoutHistory_info.setId(cursor.getInt(cursor.getColumnIndex(Workout_Contract.WorkoutHistory_Entry._ID)));
                workouts.add(workoutHistory_info);
            }
        } finally {
            if(cursor != null && !cursor.isClosed()){
                cursor.close();
            }
        }
        return workouts;
    }

    public void update_Workout(Workout_Info workout){
        ContentValues values = new ContentValues();
        values.put(Workout_Contract.Workout_Entry.COLUMN_WORKOUT, workout.getWorkout());
        values.put(Workout_Contract.Workout_Entry.COLUMN_MAX, workout.getMax());
        values.put(Workout_Contract.Workout_Entry.COLUMN_TYPE, workout.getType());
        values.put(Workout_Contract.Workout_Entry.COLUMN_PROGRESS, workout.getProgress());

        String selection = Workout_Contract.Workout_Entry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(workout.getId())};

        int count = database.update(Workout_Contract.Workout_Entry.TABLE_NAME, values, selection, selectionArgs);
    }

    public void delete_Workout(Workout_Info workout){
        delete_WorkoutHistory(workout.getId());

        String selection = Workout_Contract.Workout_Entry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(workout.getId())};

        int count = database.delete(Workout_Contract.Workout_Entry.TABLE_NAME, selection, selectionArgs);
    }

    private void delete_WorkoutHistory(long id){
        String selection = Workout_Contract.WorkoutHistory_Entry.COLUMN_WORKOUT_ID + " = ?";
        String[] selectionArgs = { String.valueOf(id)};

        database.delete(Workout_Contract.WorkoutHistory_Entry.TABLE_NAME, selection, selectionArgs);
    }

    public void delete_AllWorkouts(){
        int count = database.delete(Workout_Contract.Workout_Entry.TABLE_NAME, null, null);
    }
    public void delete_AllHistoryWorkouts(){
        int count = database.delete(Workout_Contract.WorkoutHistory_Entry.TABLE_NAME, null, null);
    }
}
