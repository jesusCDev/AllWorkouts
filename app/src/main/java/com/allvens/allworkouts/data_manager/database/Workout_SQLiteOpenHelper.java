package com.allvens.allworkouts.data_manager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Workout_SQLiteOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "workouts.db";
    private static final int VERSION_NUMBER   = 2;

    public Workout_SQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION_NUMBER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(Workout_Contract.CREATE_WORKOUT_ENTRY_TABLE);
        sqLiteDatabase.execSQL(Workout_Contract.CREATE_WORKOUT_HISTORY_ENTRY_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add difficulty_rating column to existing tables
            sqLiteDatabase.execSQL(Workout_Contract.ALTER_TABLE_ADD_DIFFICULTY_RATING);
        }
    }
}
