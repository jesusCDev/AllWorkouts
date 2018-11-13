package com.allvens.allworkouts.database;

import java.util.List;

public class Workout_Info {

    private long id;
    private String workout;
    private long type; // 0 = simple 1 = mix
    private int progress; // redo every 7 trials - update max and reset to 1 || if 0 means it doesn't count yet.
    private int max;

    private List<WorkoutHistory_Info> history;

    public Workout_Info(String workout, int max, int type, int progress){
        this.workout = workout;
        this.type = type;
        this.progress = progress;
        this.max = max;
    }



    public List<WorkoutHistory_Info> getHistory()
    {
        return history;
    }

    public void setHistory(List<WorkoutHistory_Info> history)
    {
        this.history = history;
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public long getType() {
        return type;
    }

    public void setType(long type) {
        this.type = type;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public String getWorkout() {
        return workout;
    }

    public void setWorkout(String workout) {
        this.workout = workout;
    }
}
