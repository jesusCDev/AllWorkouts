package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.settings_manager.Settings_Manager;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);

        LinearLayout ll_settings_WorkoutPositions = findViewById(R.id.ll_settings_WorkoutPositions);

        Settings_Manager manager = new Settings_Manager(this);

        manager.setUp_WorkoutsAndPositions(ll_settings_WorkoutPositions);
    }

    public void btnAction_ResetAlWorkouts(View view){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();
        wrapper.delete_AllWorkouts();
        wrapper.close();
    }
}
