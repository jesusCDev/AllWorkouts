package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.allvens.allworkouts.assets.DebuggingMethods;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Info;
import com.allvens.allworkouts.data_manager.database.Workout_Wrapper;
import com.allvens.allworkouts.log_manager.log_chart.LogChart_Manager;
import com.allvens.allworkouts.log_manager.log_chart.LogData_Entry;
import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.List;

public class LogScene_Manager {

    private String chosenWorkout;
    private Context context;
    private Workout_Wrapper wrapper;
    private Workout_Info workout;

    private LogScene_UI_Manager logScene_ui_manager;

    public LogScene_Manager(Context context, String chosenWorkout) {
        this.context = context;
        this.chosenWorkout = chosenWorkout;
        wrapper = new Workout_Wrapper(context);
        wrapper.open();
        workout = wrapper.get_Workout(chosenWorkout);
        wrapper.close();
    }

    public void setUp_UIManager(RecyclerView rvShowAllWorkoutSets, LineChart lcShowWorkoutProgress, TextView tvCurrentMax){
        logScene_ui_manager = new LogScene_UI_Manager(context, chosenWorkout, rvShowAllWorkoutSets, lcShowWorkoutProgress, tvCurrentMax);
    }

    public void update_Screen(){
        if(workout != null){

            DebuggingMethods.pop("Updating Screen");

            wrapper.open();
            logScene_ui_manager.update_Graph(get_GraphData(workout.getMax(), wrapper.get_HistoryForWorkout(workout.getId())));
            logScene_ui_manager.update_SetList(wrapper.get_HistoryForWorkout(workout.getId()));
            logScene_ui_manager.update_CurrentMax(workout.getMax());
            wrapper.close();
        }else{
            logScene_ui_manager.set_NoDataScreen();
        }
    }

    public void reset_Workout() {
        if(workout != null){
            wrapper.open();
            wrapper.delete_Workout(workout);
            wrapper.close();
            update_Screen();
        }
    }

    private ArrayList<LogData_Entry> get_GraphData(int currentMax, List<WorkoutHistory_Info> history_infoList){

        ArrayList<LogData_Entry> entries = new ArrayList<>();

        int pos = 0;
        if(history_infoList != null){
            for(int i = 0; i < history_infoList.size(); i++){
                entries.add(new LogData_Entry(i, history_infoList.get(i).getMax_value()));
                pos++;
            }
        }
        entries.add(new LogData_Entry(pos, currentMax));

        return entries;
    }
}
