package com.allvens.allworkouts.database;

public class WorkoutHistory_Info {

    private long id;

    private int first_value;
    private int second_value;
    private int third_value;
    private int forth_value;
    private int fifth_value;

    private int max_value;

    public WorkoutHistory_Info(int first_value, int second_value, int third_value, int forth_value, int fifth_value, int max_value){

        this.first_value = first_value;
        this.second_value = second_value;
        this.third_value = third_value;
        this.forth_value = forth_value;
        this.fifth_value = fifth_value;

        this.max_value = max_value;

    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getFirst_value() {
        return first_value;
    }

    public void setFirst_value(int first_value) {
        this.first_value = first_value;
    }

    public int getSecond_value() {
        return second_value;
    }

    public void setSecond_value(int second_value) {
        this.second_value = second_value;
    }

    public int getThird_value() {
        return third_value;
    }

    public void setThird_value(int third_value) {
        this.third_value = third_value;
    }

    public int getForth_value() {
        return forth_value;
    }

    public void setForth_value(int forth_value) {
        this.forth_value = forth_value;
    }

    public int getFifth_value() {
        return fifth_value;
    }

    public void setFifth_value(int fifth_value) {
        this.fifth_value = fifth_value;
    }

    public int getMax_value() {
        return max_value;
    }

    public void setMax_value(int max_value) {
        this.max_value = max_value;
    }
}
