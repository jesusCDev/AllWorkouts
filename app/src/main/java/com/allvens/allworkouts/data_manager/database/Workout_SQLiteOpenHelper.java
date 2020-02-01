package com.allvens.allworkouts.data_manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Workout_SQLiteOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "workouts.db";
    private static final int VERSION_NUMBER = 1;

    public Workout_SQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Workout_Contract.CREATE_WORKOUT_ENTRY_TABLE);
        sqLiteDatabase.execSQL(Workout_Contract.CREATE_WORKOUT_HISTORY_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Workout_Contract.Workout_Entry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Workout_Contract.WorkoutHistory_Entry.TABLE_NAME);

        onCreate(sqLiteDatabase);
    }
}
