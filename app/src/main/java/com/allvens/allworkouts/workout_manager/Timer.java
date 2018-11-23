package com.allvens.allworkouts.workout_manager;

import android.os.Handler;

public class Timer {

    private UI_Manager ui_manager;

    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime = 0;
    private boolean timerRunning = false;

    public Timer(UI_Manager ui_manager){
        this.ui_manager = ui_manager;
    }

    public boolean get_TimerRunning(){
        return timerRunning;
    }

    public void stop_timer(){
        timerRunning = false;
        try{
            timerHandler.removeCallbacks(timerRunnable);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void start_timer() {
        timerRunning = true;
        ui_manager.play_basicSound();
        ui_manager.vibrate();

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void onFinish(){
        stop_timer();
        ui_manager.vibrate();
        ui_manager.play_basicSound();
        ui_manager.changeScreen_Workout();
    }

    public void create_timer(int workoutTime) {
        final int time_forLevel = workoutTime;

        timerHandler  = new Handler();
        timerRunnable = new Runnable() {
            @Override
            public void run() {
                // time passed
                long millis = System.currentTimeMillis() - startTime;
                long time_tracker = (time_forLevel - millis);
                long secondsLeft = (time_tracker / 1000);

                // update ui
                ui_manager.set_TimeTV(secondsLeft);

                timerHandler.postDelayed(this, 1000);
                if(time_tracker < 0){
                    onFinish();
                }

            }

        };
    }

}