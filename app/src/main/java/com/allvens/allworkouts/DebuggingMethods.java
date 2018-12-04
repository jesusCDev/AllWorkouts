package com.allvens.allworkouts;

import android.util.Log;

public class DebuggingMethods {

    public static void pop(String message){
        Log.d("Bug", message);
    }

    public static void pop(String bugKey, String message){
        Log.d(bugKey, message);
    }
}
