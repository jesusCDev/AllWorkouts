package com.allvens.allworkouts.settings_manager;

public class WorkoutPosAndStatus {

    private String name;
    private String posPrefKey;
    private String statPrefKey;
    private int resourceID;
    private int position;
    private boolean turnOnStatus;

    public WorkoutPosAndStatus(String name, String posPrefKey, String statPrefKey, int resourceID) {
        this.name = name;
        this.posPrefKey = posPrefKey;
        this.statPrefKey = statPrefKey;
        this.resourceID = resourceID;
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

    public int getResourceID() {
        return resourceID;
    }

    public void setResourceID(int resourceID) {
        this.resourceID = resourceID;
    }

    public boolean get_TurnOnStatus() {
        return turnOnStatus;
    }

    public void set_TurnOnStatus(boolean turnOnStatus) {
        this.turnOnStatus = turnOnStatus;
    }

    public String getPosPrefKey() {
        return posPrefKey;
    }

    public void setPosPrefKey(String posPrefKey) {
        this.posPrefKey = posPrefKey;
    }

    public String getStatPrefKey() {
        return statPrefKey;
    }

    public void setStatPrefKey(String statPrefKey) {
        this.statPrefKey = statPrefKey;
    }
}
