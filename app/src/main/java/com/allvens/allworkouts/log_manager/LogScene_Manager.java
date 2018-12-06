package com.allvens.allworkouts.log_manager;

import android.content.Context;

public class LogScene_Manager {

    public LogScene_Manager(Context context, String chosenWorkout) {
        LogScene_UI_Manager logScene_ui_manager = new LogScene_UI_Manager(context, chosenWorkout);
    }
}
