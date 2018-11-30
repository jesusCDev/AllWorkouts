package com.allvens.allworkouts;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.home_manager.HomeManager;

import java.util.List;

public class HomeActivity extends AppCompatActivity {



    private HomeManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        TextView tv_CurrentWorkout = findViewById(R.id.tv_home_CurrentWorkout);
        Button btn_ChangeWorkouts = findViewById(R.id.btn_ChangeWorkouts);
        LinearLayoutCompat ll_home_WorkoutChooser = findViewById(R.id.ll_home_WorkoutChooser);

        manager = new HomeManager(this, tv_CurrentWorkout, btn_ChangeWorkouts, ll_home_WorkoutChooser);
    }

    public void btnAction_changeWorkouts(View view) {
        manager.clear_WorkoutChanger();
        if(!manager.get_WorkoutChooserOpen()){
            manager.open_WorkoutChanger();
        }
        manager.set_WorkoutChooserOpen(!manager.get_WorkoutChooserOpen());
    }

    public void btnAction_StartWorkout(View view) {
        manager.start_Workout();
    }

    public void btnAction_Settings(View view) {
        startActivity(new Intent(this, SettingsActivity.class));
    }

    public void btnAction_Log(View view) {
    }

}
