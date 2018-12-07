package com.allvens.allworkouts.log_manager.log_chart;

public class LogData_Entry {

    private float position;
    private float value;

    public LogData_Entry(int position, float value){
        this.position = position;
        this.value = value;
    }


    public float getPosition() {
        return position;
    }

    public void setPosition(float position) {
        this.position = position;
    }

    public float getValue() {
        return value;
    }

    public void setValue(float value) {
        this.value = value;
    }
}
