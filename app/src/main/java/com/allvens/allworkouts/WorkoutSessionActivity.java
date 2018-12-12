package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

        LinearLayout llTimeImageHolder = findViewById(R.id.ll_workout_timeImageHolder);

        TextView tvValue1 = findViewById(R.id.tv_workout_Value1);
        TextView tvValue2 = findViewById(R.id.tv_workout_Value2);
        TextView tvValue3 = findViewById(R.id.tv_workout_Value3);
        TextView tvValue4 = findViewById(R.id.tv_workout_Value4);
        TextView tvValue5 = findViewById(R.id.tv_workout_Value5);

        Button btn_workout_CompleteTask = findViewById(R.id.btn_workout_CompleteTask);

        manager = new WorkoutSessionScene_Manager(this, getIntent().getExtras().get(Constants.CHOSEN_WORKOUT_EXTRA_KEY).toString());
        manager.setUp_UiManager(tv_workout_WorkoutName, llTimeImageHolder, tvValue1, tvValue2, tvValue3, tvValue4, tvValue5, btn_workout_CompleteTask);
        manager.setUp_Timer();
        manager.start_Screen();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void btnAction_ChangeActivities(View view){
        manager.update_Screen();
    }
}
