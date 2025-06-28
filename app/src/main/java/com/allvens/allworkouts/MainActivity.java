package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.home_manager.HomeManager;

public class MainActivity extends AppCompatActivity {

    private HomeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView currentWorkoutText         = findViewById(R.id.tv_home_CurrentWorkout);
        ImageView ivWorkout                 = findViewById(R.id.iv_home_WorkoutShow);
        ImageButton changeWorkoutButton     = findViewById(R.id.btn_ChangeWorkouts);
        LinearLayoutCompat llWorkoutChooser = findViewById(R.id.ll_home_WorkoutChooser);
        manager                             = new HomeManager(
                this,
                currentWorkoutText,
                ivWorkout,
                changeWorkoutButton,
                llWorkoutChooser
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.refreshWorkouts();

        if(!manager.check_IfCurrentWorkoutExistNow()) {
            manager.setWorkout(manager.getFirstWorkout());
        }
    }

    public void onChangeWorkoutsClicked(View view) {
        manager.clearWorkoutChanger();

        if(!manager.getWorkoutChooserOpen()){
            manager.openWorkoutChanger();
        }

        manager.setWorkoutChooserOpen(!manager.getWorkoutChooserOpen());
    }

    public void onStartWorkoutClicked(View view) {
        manager.gotoWorkoutScene();
    }

    public void onSettingsClicked(View view) {
        manager.clearWorkoutChanger();
        manager.setWorkoutChooserOpen(false);
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void onLogClicked(View view) {
        manager.gotoLogScreen();
    }
}
