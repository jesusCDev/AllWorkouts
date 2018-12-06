package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.log_manager.LogScene_Manager;

public class LogActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        String chosenWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        LogScene_Manager logScene_manager = new LogScene_Manager(this, chosenWorkout);

    }

    public void btnAction_ResetWorkoutToZero(View view) {

    }
}
