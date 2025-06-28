package com.allvens.allworkouts.data_manager.database;

import java.util.List;

/**
 * Workout DataBase Entry
 */
public class Workout_Info {

    private long id;
    private String workout;
    private int type; // 0 = simple 1 = mix
    private int progress; // redo every 7 trials - update max and reset to 1 || if 0 means it doesn't count yet.
    private int max;

    private List<WorkoutHistory_Info> history;

    public Workout_Info(String workout, int max, int type, int progress){
        this.workout  = workout;
        this.type     = type;
        this.progress = progress;
        this.max      = max;
    }

    public Workout_Info(String workout, int max, int type, int progress, List<WorkoutHistory_Info> history){
        this.workout  = workout;
        this.type     = type;
        this.progress = progress;
        this.max      = max;
        this.history  = history;
    }

    public List<WorkoutHistory_Info> getHistory()
    {
        return history;
    }

    public void setHistory(List<WorkoutHistory_Info> history)
    {
        this.history = history;
    }

    public void add_History(WorkoutHistory_Info history_info){
        history.add(history_info);
    }

    public void setId(long id){
        this.id = id;
    }

    public long getId(){
        return id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
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
