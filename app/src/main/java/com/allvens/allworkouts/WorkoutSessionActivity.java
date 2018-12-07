package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.workout_session_manager.WorkoutSessionScene_Manager;

public class WorkoutSessionActivity extends AppCompatActivity {

    private WorkoutSessionScene_Manager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_workout_session);

        TextView tv_workout_WorkoutName = findViewById(R.id.tv_workout_WorkoutName);
        LinearLayout ll_workout_timeImageHolder = findViewById(R.id.ll_workout_timeImageHolder);
        LinearLayout ll_workout_ValueHolder = findViewById(R.id.ll_workout_ValueHolder);
        Button btn_workout_CompleteTask = findViewById(R.id.btn_workout_CompleteTask);

        manager = new WorkoutSessionScene_Manager(this, getIntent().getExtras().get(Constants.CHOSEN_WORKOUT_EXTRA_KEY).toString());
        manager.setUp_UiManager(tv_workout_WorkoutName, ll_workout_timeImageHolder, ll_workout_ValueHolder, btn_workout_CompleteTask);
        manager.setUp_Timer();
        manager.start_Screen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    public void btnAction_ChangeActivities(View view){
        manager.update_Screen();
    }
}
