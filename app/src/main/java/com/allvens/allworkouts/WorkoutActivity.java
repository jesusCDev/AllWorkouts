package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.workout_manager.WorkoutSession_Manager;

public class WorkoutActivity extends AppCompatActivity {

    private WorkoutSession_Manager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_session);

        TextView tv_wokrout_WorkoutName = findViewById(R.id.tv_wokrout_WorkoutName);
        LinearLayout ll_workout_timeImageHolder = findViewById(R.id.ll_workout_timeImageHolder);
        Button btn_workout_CompleteTask = findViewById(R.id.btn_workout_CompleteTask);

        manager = new WorkoutSession_Manager(this, getIntent().getExtras().get("chosenWorkout").toString());
        manager.update_UI();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
