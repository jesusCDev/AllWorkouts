package com.allvens.allworkouts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.home_manager.HomeScene_Manager;

public class MainActivity extends AppCompatActivity {

    private HomeScene_Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tv_CurrentWorkout = findViewById(R.id.tv_home_CurrentWorkout);
        Button btn_ChangeWorkouts = findViewById(R.id.btn_ChangeWorkouts);
        LinearLayoutCompat ll_home_WorkoutChooser = findViewById(R.id.ll_home_WorkoutChooser);

        manager = new HomeScene_Manager(this, tv_CurrentWorkout, btn_ChangeWorkouts, ll_home_WorkoutChooser);

    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setUp_WorkoutsPos();
        manager.update_Workout(manager.get_Workouts()[0]);
    }

    public void btnAction_changeWorkouts(View view) {
        manager.clear_WorkoutChanger();
        if(!manager.get_WorkoutChooserOpen()){
            manager.open_WorkoutChanger();
        }
        manager.set_WorkoutChooserOpen(!manager.get_WorkoutChooserOpen());
    }

    public void btnAction_StartWorkout(View view) {
        manager.goto_WorkoutScene();
    }

    public void btnAction_Settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void btnAction_Log(View view) {
        manager.goto_LogScene();
    }
}
