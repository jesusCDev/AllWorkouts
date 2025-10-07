package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.log_manager.log_chart.LogChartManager;
import com.allvens.allworkouts.log_manager.log_chart.LineChartDataEntry;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogUIManager {

    private Context context;
    private String chosenWorkout;
    private RecyclerView rvShowAllWorkoutSets;
    private TextView tvCurrentMax;
    private TextView tvType;
    private LogChartManager logChart_manager;

    public LogUIManager(Context context, String chosenWorkout, RecyclerView rvShowAllWorkoutSets, LineChart lcShowWorkoutProgress, TextView tvCurrentMax, TextView tvType) {
        this.context              = context;
        this.chosenWorkout        = chosenWorkout;
        this.rvShowAllWorkoutSets = rvShowAllWorkoutSets;
        this.tvCurrentMax         = tvCurrentMax;
        this.tvType               = tvType;
        logChart_manager          = new LogChartManager(context, lcShowWorkoutProgress);
    }

    /********** Max - Methods **********/

    public void update_CurrentMax(int max) {
        tvCurrentMax.setText(max + " reps");
    }

    public void update_CurrentType(int type) {
        String sType = "Simple";

        if(type == 1) {
            sType = "Mix";
        }

        tvType.setText(sType);
    }

    public void update_Graph(ArrayList<LineChartDataEntry> totalSets) {
        logChart_manager.reset_Chart();
        logChart_manager.setUp_ChartValues();
        logChart_manager.create_Chart(totalSets);
    }

    public void reset_GraphToZero(){
        logChart_manager.reset_Chart();
    }

    public void update_SetList(List<WorkoutHistoryInfo> historyForWorkout) {
        Collections.reverse(historyForWorkout);

        ArrayList<WorkoutHistoryInfo> list = new ArrayList<>();
        list.addAll(historyForWorkout);

        // Set up layout manager with proper configuration
        LinearLayoutManager layoutManager = new LinearLayoutManager(context);
        layoutManager.setAutoMeasureEnabled(true);
        rvShowAllWorkoutSets.setLayoutManager(layoutManager);
        
        // Configure RecyclerView for better performance and layout
        rvShowAllWorkoutSets.setHasFixedSize(false);
        rvShowAllWorkoutSets.setNestedScrollingEnabled(true);
        
        // Disable item animator to prevent layout issues
        rvShowAllWorkoutSets.setItemAnimator(null);
        
        // Set adapter and force immediate layout
        SetListRecyclerViewAdapter adapter = new SetListRecyclerViewAdapter(context, list);
        rvShowAllWorkoutSets.setAdapter(adapter);
        
        // Multiple approaches to force proper layout and prevent squishing
        // Approach 1: Immediate layout request
        rvShowAllWorkoutSets.requestLayout();
        
        // Approach 2: Post-layout fixes
        rvShowAllWorkoutSets.post(() -> {
            if (rvShowAllWorkoutSets.getAdapter() != null && rvShowAllWorkoutSets.getAdapter().getItemCount() > 0) {
                // Force proper measurement and layout
                rvShowAllWorkoutSets.getLayoutManager().requestLayout();
                rvShowAllWorkoutSets.requestLayout();
                rvShowAllWorkoutSets.invalidate();
                
                // Scroll to top to trigger proper layout calculation
                rvShowAllWorkoutSets.scrollToPosition(0);
            }
        });
        
        // Approach 3: Additional passes with delays to ensure proper rendering
        rvShowAllWorkoutSets.postDelayed(() -> {
            if (rvShowAllWorkoutSets.getAdapter() != null && rvShowAllWorkoutSets.getAdapter().getItemCount() > 0) {
                rvShowAllWorkoutSets.getLayoutManager().requestLayout();
                rvShowAllWorkoutSets.requestLayout();
                // Force remeasure of first visible items
                for (int i = 0; i < Math.min(3, rvShowAllWorkoutSets.getAdapter().getItemCount()); i++) {
                    rvShowAllWorkoutSets.getLayoutManager().findViewByPosition(i);
                }
            }
        }, 100);
        
        // Approach 4: Final safety net - force layout after a longer delay
        rvShowAllWorkoutSets.postDelayed(() -> {
            rvShowAllWorkoutSets.requestLayout();
        }, 200);
    }

    public void reset_SetList(){
        rvShowAllWorkoutSets.setVisibility(View.INVISIBLE);
    }
}
