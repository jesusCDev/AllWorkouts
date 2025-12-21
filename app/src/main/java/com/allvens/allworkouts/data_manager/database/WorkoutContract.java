package com.allvens.allworkouts.data_manager.database;

import android.provider.BaseColumns;

final class WorkoutContract {

    public WorkoutContract(){}

    static final String CREATE_WORKOUT_ENTRY_TABLE =
            "CREATE TABLE " + Workout_Entry.TABLE_NAME +
                    " ( " +
                    Workout_Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Workout_Entry.COLUMN_WORKOUT+ " TEXT NOT NULL, " +
                    Workout_Entry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_PROGRESS + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_MAX + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_DIFFICULTY_RATING + " INTEGER DEFAULT 1000, " +
                    "UNIQUE ( " + Workout_Entry._ID + ") ON CONFLICT REPLACE )";

    static final String CREATE_WORKOUT_ENTRY_TABLE_V2 =
            "CREATE TABLE " + Workout_Entry.TABLE_NAME +
                    " ( " +
                    Workout_Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    Workout_Entry.COLUMN_WORKOUT+ " TEXT NOT NULL, " +
                    Workout_Entry.COLUMN_TYPE + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_PROGRESS + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_MAX + " INTEGER NOT NULL, " +
                    Workout_Entry.COLUMN_DIFFICULTY_RATING + " INTEGER DEFAULT 1000, " +
                    "UNIQUE ( " + Workout_Entry._ID + ") ON CONFLICT REPLACE )";

    static final String ALTER_TABLE_ADD_DIFFICULTY_RATING =
            "ALTER TABLE " + Workout_Entry.TABLE_NAME + " ADD COLUMN " +
                    Workout_Entry.COLUMN_DIFFICULTY_RATING + " INTEGER DEFAULT 1000";

    static final String CREATE_WORKOUT_HISTORY_ENTRY_TABLE =
            "CREATE TABLE " + WorkoutHistory_Entry.TABLE_NAME +
                    " ( " +
                    WorkoutHistory_Entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    WorkoutHistory_Entry.COLUMN_WORKOUT_ID + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_FIRST + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_SECOND + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_THIRD + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_FORTH + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_FIFTH + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_MAX + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_COMPLETION_DATE + " INTEGER NOT NULL, " +
                    WorkoutHistory_Entry.COLUMN_DURATION_SECONDS + " INTEGER, " +
                    "UNIQUE ( " + WorkoutHistory_Entry._ID + ") ON CONFLICT REPLACE )";

    static final String ALTER_TABLE_ADD_COMPLETION_DATE =
            "ALTER TABLE " + WorkoutHistory_Entry.TABLE_NAME + " ADD COLUMN " +
                    WorkoutHistory_Entry.COLUMN_COMPLETION_DATE + " INTEGER DEFAULT " + (System.currentTimeMillis() / 1000);

    static final String ALTER_TABLE_ADD_DURATION_SECONDS =
            "ALTER TABLE " + WorkoutHistory_Entry.TABLE_NAME + " ADD COLUMN " +
                    WorkoutHistory_Entry.COLUMN_DURATION_SECONDS + " INTEGER DEFAULT NULL";

    public static class Workout_Entry implements BaseColumns{
        public static final String TABLE_NAME         = "workout_info";
        public static final String COLUMN_WORKOUT     = "workout";
        public static final String COLUMN_TYPE        = "type";
        public static final String COLUMN_PROGRESS    = "progress";
        public static final String COLUMN_MAX         = "max";
        public static final String COLUMN_DIFFICULTY_RATING = "difficulty_rating";
    }
    
public static class WorkoutHistory_Entry implements BaseColumns{
        public static final String TABLE_NAME            = "workout_history";
        public static final String COLUMN_WORKOUT_ID     = "id";
        public static final String COLUMN_FIRST          = "first_value";
        public static final String COLUMN_SECOND         = "second_value";
        public static final String COLUMN_THIRD          = "third_value";
        public static final String COLUMN_FORTH          = "fourth_value";
        public static final String COLUMN_FIFTH          = "fifth_value";
        public static final String COLUMN_MAX            = "max_value";
        public static final String COLUMN_COMPLETION_DATE = "completion_date";
        public static final String COLUMN_DURATION_SECONDS = "duration_seconds";
    }

}
