package com.allvens.allworkouts.assets;

import android.util.Log;

// todo delete - used for debuging purposes
public class DebuggingMethods {

    public static void pop(String message){
        Log.d("Bug", message);
    }

    public static void pop(String bugKey, String message){
        Log.d(bugKey, message);
    }
}
