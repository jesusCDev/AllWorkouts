package com.allvens.allworkouts.settings_manager;

public class WorkoutPos {

    private String name;
    private String prefName;
    private String resourceID;
    private int position;
    private boolean turnOnStatus;

    public WorkoutPos() {
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResourceID() {
        return resourceID;
    }

    public void setResourceID(String resourceID) {
        this.resourceID = resourceID;
    }

    public boolean isTurnOnStatus() {
        return turnOnStatus;
    }

    public void setTurnOnStatus(boolean turnOnStatus) {
        this.turnOnStatus = turnOnStatus;
    }

    public String getPrefName() {
        return prefName;
    }

    public void setPrefName(String prefName) {
        this.prefName = prefName;
    }
}
