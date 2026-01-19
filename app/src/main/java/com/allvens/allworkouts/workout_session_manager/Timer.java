package com.allvens.allworkouts.workout_session_manager;

import android.os.Handler;

public class Timer {

    /**
     * Callback interface for timer tick notifications
     */
    public interface TimerTickCallback {
        void onTimerTick(int secondsRemaining);
    }

    private WorkoutSessionUIManager workoutSessionUi_manager;
    private TimerTickCallback timerTickCallback;

    private Handler timerHandler;
    private Runnable timerRunnable;
    private long startTime = 0;
    private boolean timerRunning = false;

    public Timer(WorkoutSessionUIManager workoutSessionUi_manager){
        this.workoutSessionUi_manager = workoutSessionUi_manager;
    }

    /**
     * Set the timer tick callback for notification updates
     */
    public void setTimerTickCallback(TimerTickCallback callback) {
        this.timerTickCallback = callback;
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
        workoutSessionUi_manager.playSound();
        workoutSessionUi_manager.changeScreenToWorkout();
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
                workoutSessionUi_manager.setTimeTV(secondsLeft);

                // notify callback for notification updates
                if (timerTickCallback != null && secondsLeft >= 0) {
                    timerTickCallback.onTimerTick((int) secondsLeft);
                }

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

    /**
     * Add extra time to the running timer
     * @param extraSeconds seconds to add
     */
    public void addExtraTime(int extraSeconds) {
        if (timerRunning) {
            // Move the start time forward to effectively add more time
            startTime += (extraSeconds * 1000);
        }
    }
}