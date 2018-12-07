package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.log_manager.log_chart.LogChart_Manager;
import com.allvens.allworkouts.log_manager.log_chart.LogData_Entry;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogScene_UI_Manager {

    private Context context;
    private String chosenWorkout;
    private RecyclerView rvShowAllWorkoutSets;
    private TextView tvCurrentMax;
    private LogChart_Manager logChart_manager;

    public LogScene_UI_Manager(Context context, String chosenWorkout, RecyclerView rvShowAllWorkoutSets, LineChart lcShowWorkoutProgress, TextView tvCurrentMax) {
        this.context = context;
        this.chosenWorkout = chosenWorkout;
        this.rvShowAllWorkoutSets = rvShowAllWorkoutSets;
        this.tvCurrentMax = tvCurrentMax;


        logChart_manager = new LogChart_Manager(context, lcShowWorkoutProgress);
    }

    public void update_Graph(ArrayList<LogData_Entry> graphData) {
        logChart_manager.reset_Chart();
        logChart_manager.setUp_ChartValues();
        logChart_manager.create_Chart(graphData);
    }

    public void set_NoDataScreen() {
        update_CurrentMax(0);
    }

    public void update_SetList(List<WorkoutHistory_Info> historyForWorkout) {

        Collections.reverse(historyForWorkout);
        ArrayList<WorkoutHistory_Info> list = new ArrayList<>();
        list.addAll(historyForWorkout);

        ListRecyclerView_Adapter adapter = new ListRecyclerView_Adapter(context, list);
        rvShowAllWorkoutSets.setAdapter(adapter);
        rvShowAllWorkoutSets.setLayoutManager(new LinearLayoutManager(context));
    }

    public void update_CurrentMax(int max) {
        tvCurrentMax.setText("Current Max: " + max);
    }
}
