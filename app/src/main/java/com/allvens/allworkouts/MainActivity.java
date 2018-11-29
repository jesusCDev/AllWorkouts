package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.allvens.allworkouts.assets.PreferencesValues;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        start_Timer();
    }

    private void change_screens(){
        Intent intent;
        if(this.getSharedPreferences(PreferencesValues.PREFS_NAMES, MODE_PRIVATE).getBoolean(PreferencesValues.FIRST_TIME_USING, true)){
            intent = new Intent(this, StartingActivity.class);
        }else{
            intent = new Intent(this, HomeActivity.class);
        }
        this.startActivity(intent);
    }

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable;

    public void start_Timer(){
        create_Timer();
        timerHandler.postDelayed(timerRunnable, 0);
    }

    private void create_Timer(){

        final int timer_value = 0;
//        final int timer_value = 3000; todo set this again
        final long startTime = System.currentTimeMillis();

        timerRunnable = new Runnable() {
            @Override
            public void run() {
                long millis = System.currentTimeMillis() - startTime;
                timerHandler.postDelayed(this, 1000);
                long timer_tracker = timer_value - millis;

                if(timer_tracker < 0){
                    timerHandler.removeCallbacks(timerRunnable);
                    change_screens();
                }
            }
        };

    }
}
