package com.allvens.allworkouts.log_manager.log_chart;

public class LineChartData_Entry {

    private int position;
    private int value;

    public LineChartData_Entry(int position, int value){
        this.position = position;
        this.value = value;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }
}
