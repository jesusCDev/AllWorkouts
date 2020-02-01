package com.allvens.allworkouts.workout_session_manager;

import android.os.Handler;

public class Timer {

    private WorkoutSession_UI_Manager workoutSessionUi_manager;

    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime = 0;
    private boolean timerRunning = false;

    public Timer(WorkoutSession_UI_Manager workoutSessionUi_manager){
        this.workoutSessionUi_manager = workoutSessionUi_manager;
    }

    public boolean get_TimerRunning(){
        return timerRunning;
    }

    /****************************************
     /**** NON-TIMER METHODS
     ****************************************/

    private void onFinish(){
        stop_timer();
        workoutSessionUi_manager.vibrate();
        workoutSessionUi_manager.play_Sound();
        workoutSessionUi_manager.changeScreen_Workout();
    }

    /****************************************
     /**** TIMER METHODS
     ****************************************/

    /********** Create Timer **********/

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
                workoutSessionUi_manager.set_TimeTV(secondsLeft);

                timerHandler.postDelayed(this, 1000);
                if(time_tracker < 0){
                    onFinish();
                }

            }

        };
    }

    /********** Timer Functionality Methods **********/

    public void start_timer() {
        timerRunning = true;

        startTime = System.currentTimeMillis();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    public void stop_timer(){
        timerRunning = false;
        try{
            timerHandler.removeCallbacks(timerRunnable);
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }
}