package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.allvens.allworkouts.home_manager.Home_Manager;

public class MainActivity extends AppCompatActivity {

    private Home_Manager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tv_CurrentWorkout = findViewById(R.id.tv_home_CurrentWorkout);
        ImageView iv_Workout = findViewById(R.id.iv_home_WorkoutShow);
        ImageButton btn_ChangeWorkouts = findViewById(R.id.btn_ChangeWorkouts);
        LinearLayoutCompat ll_home_WorkoutChooser = findViewById(R.id.ll_home_WorkoutChooser);

        manager = new Home_Manager(this, tv_CurrentWorkout, iv_Workout, btn_ChangeWorkouts, ll_home_WorkoutChooser);
    }

    @Override
    protected void onResume() {
        super.onResume();
        manager.setUp_WorkoutsPos();
        if(!manager.check_IfCurrentWorkoutExistNow()) manager.set_Workout(manager.get_FirstWorkout());
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    public void btnAction_changeWorkouts(View view) {
        manager.clear_WorkoutChanger();
        if(!manager.get_WorkoutChooserOpen()){
            manager.open_WorkoutChanger();
        }
        manager.set_WorkoutChooserOpen(!manager.get_WorkoutChooserOpen());
    }

    /********** Screen Changers **********/

    public void btnAction_StartWorkout(View view) {
        manager.goto_WorkoutScene();
    }

    public void btnAction_Settings(View view) {
        manager.clear_WorkoutChanger();
        manager.set_WorkoutChooserOpen(false);

        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void btnAction_Log(View view) {
        manager.goto_LogScreen();
    }
}
