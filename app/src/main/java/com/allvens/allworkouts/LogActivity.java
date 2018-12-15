package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.log_manager.Log_Manager;
import com.github.mikephil.charting.charts.LineChart;

public class LogActivity extends AppCompatActivity {

    private Log_Manager log_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        RecyclerView rvShowAllWorkoutSets = findViewById(R.id.rv_log_ShowAllWorkoutSets);
        LineChart lcShowWorkoutProgress = findViewById(R.id.lc_log_ShowWorkoutProgression);
        TextView tvCurrentMax = findViewById(R.id.tv_settings_CurrentMaxContainer);

        String chosenWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);

        log_manager = new Log_Manager(this, chosenWorkout);
        log_manager.setUp_UIManager(rvShowAllWorkoutSets, lcShowWorkoutProgress, tvCurrentMax);
        log_manager.update_Screen();

    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    public void btnAction_ResetWorkoutToZero(View view) {
        log_manager.reset_Workout();
    }

    public void btnAction_EditCurrentMaxValue(View view) {
        log_manager.update_MaxValue();
    }
}
