package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.log_manager.log_chart.LogChart_Manager;
import com.allvens.allworkouts.log_manager.log_chart.LineChartData_Entry;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Log_UI_Manager {

    private Context context;
    private String chosenWorkout;
    private RecyclerView rvShowAllWorkoutSets;
    private TextView tvCurrentMax;
    private TextView tvType;
    private LogChart_Manager logChart_manager;

    public Log_UI_Manager(Context context, String chosenWorkout, RecyclerView rvShowAllWorkoutSets, LineChart lcShowWorkoutProgress, TextView tvCurrentMax, TextView tvType) {
        this.context              = context;
        this.chosenWorkout        = chosenWorkout;
        this.rvShowAllWorkoutSets = rvShowAllWorkoutSets;
        this.tvCurrentMax         = tvCurrentMax;
        this.tvType               = tvType;
        logChart_manager          = new LogChart_Manager(context, lcShowWorkoutProgress);
    }

    /********** Max - Methods **********/

    public void update_CurrentMax(int max) {
        tvCurrentMax.setText("Current Max: " + max);
    }

    public void update_CurrentType(int type) {
        String sType = "Simple";

        if(type == 1) {
            sType = "Mix";
        }

        tvType.setText("Type: " + sType);
    }

    public void update_Graph(ArrayList<LineChartData_Entry> totalSets) {
        logChart_manager.reset_Chart();
        logChart_manager.setUp_ChartValues();
        logChart_manager.create_Chart(totalSets);
    }

    public void reset_GraphToZero(){
        logChart_manager.reset_Chart();
    }

    public void update_SetList(List<WorkoutHistory_Info> historyForWorkout) {
        Collections.reverse(historyForWorkout);

        ArrayList<WorkoutHistory_Info> list = new ArrayList<>();

        list.addAll(historyForWorkout);

        SetListRecyclerView_Adapter adapter = new SetListRecyclerView_Adapter(context, list);

        rvShowAllWorkoutSets.setAdapter(adapter);
        rvShowAllWorkoutSets.setLayoutManager(new LinearLayoutManager(context));
    }

    public void reset_SetList(){
        rvShowAllWorkoutSets.setVisibility(View.INVISIBLE);
    }
}
