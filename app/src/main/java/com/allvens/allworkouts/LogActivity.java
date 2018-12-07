package com.allvens.allworkouts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.log_manager.LogScene_Manager;
import com.github.mikephil.charting.charts.LineChart;

public class LogActivity extends AppCompatActivity {

    private LogScene_Manager logScene_manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        RecyclerView rvShowAllWorkoutSets = findViewById(R.id.rv_log_ShowAllWorkoutSets);
        LineChart lcShowWorkoutProgress = findViewById(R.id.lc_log_ShowWorkoutProgression);
        TextView tvCurrentMax = findViewById(R.id.tv_settings_CurrentMaxContainer);

        String chosenWorkout = getIntent().getExtras().getString(Constants.CHOSEN_WORKOUT_EXTRA_KEY);
        logScene_manager = new LogScene_Manager(this, chosenWorkout);
        logScene_manager.setUp_UIManager(rvShowAllWorkoutSets, lcShowWorkoutProgress, tvCurrentMax);
        logScene_manager.update_Screen();

    }

    public void btnAction_ResetWorkoutToZero(View view) {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        new Toast(LogActivity.this).makeText(LogActivity.this, "Workout Data Deleted", Toast.LENGTH_SHORT).show();
                        logScene_manager.reset_Workout();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        new Toast(LogActivity.this).makeText(LogActivity.this, "Nothing was deleted", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to reset workout to zero?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
}
