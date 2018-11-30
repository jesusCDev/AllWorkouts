package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;

public class SettingsActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivty_settings);
    }

    public void btnAction_ResetAlWorkouts(View view){
        Workout_Wrapper wrapper = new Workout_Wrapper(this);
        wrapper.open();
        wrapper.delete_AllWorkouts();
        wrapper.delete_AllHistoryWorkouts();
        wrapper.close();
    }
}
