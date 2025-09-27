package com.allvens.allworkouts.log_manager;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;
import android.widget.Toast;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.WorkoutMaximumActivity;
import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.data_manager.database.WorkoutHistory_Info;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.log_manager.log_chart.LineChartData_Entry;
import com.github.mikephil.charting.charts.LineChart;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class Log_Manager {

    private String chosenWorkout;
    private Context context;
    private WorkoutWrapper wrapper;
    private WorkoutInfo workout;
    private Log_UI_Manager log_ui_manager;

    public Log_Manager(Context context, String chosenWorkout) {
        this.context       = context;
        this.chosenWorkout = chosenWorkout;
        wrapper            = new WorkoutWrapper(context);

        wrapper.open();

        workout = wrapper.getWorkout(chosenWorkout);

        wrapper.close();
    }

    public void setUp_UIManager(RecyclerView rvShowAllWorkoutSets, LineChart lcShowWorkoutProgress, TextView tvCurrentMax, TextView tvType){
        log_ui_manager = new Log_UI_Manager(context, chosenWorkout, rvShowAllWorkoutSets, lcShowWorkoutProgress, tvCurrentMax, tvType);
    }

    public void update_Screen(){
        if(workout != null) {
            wrapper.open();
            log_ui_manager.update_Graph(get_GraphData_TotalReps(wrapper.getHistoryForWorkout(workout.getId())));
            log_ui_manager.update_SetList(wrapper.getHistoryForWorkout(workout.getId()));
            log_ui_manager.update_CurrentMax(workout.getMax());
            log_ui_manager.update_CurrentType(workout.getType());
            wrapper.close();
        }
        else{
            log_ui_manager.reset_GraphToZero();
            log_ui_manager.reset_SetList();
            log_ui_manager.update_CurrentMax(0);
        }
    }

    public void reset_Workout(){
        if(workout != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            showDarkToast("Workout Data Deleted");
                            delete_Workout();
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            showDarkToast("Nothing was deleted");
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
            builder.setMessage("Are you sure you want to reset workout to zero?")
                    .setTitle("Reset Workout")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener)
                    .show();
        }else{
            showDarkToast("First Start Workout");
        }
    }

    private void delete_Workout() {
        if(workout != null){
            wrapper.open();
            wrapper.deleteWorkout(workout);
            wrapper.close();

            workout = null;

            update_Screen();
        }
    }

    private ArrayList<LineChartData_Entry> get_GraphData_TotalReps(List<WorkoutHistory_Info> history_infoList){

        ArrayList<LineChartData_Entry> entries = new ArrayList<>();

        if(history_infoList != null) {
            int iter = 0;

            if(history_infoList.size() > 21){
                iter = (history_infoList.size() - 21);
            }

            for(int i = iter; i < history_infoList.size(); i++){
                entries.add(new LineChartData_Entry(i, history_infoList.get(i).get_TotalReps()));
            }
        }

        return entries;
    }

    public void update_Type(){
        if(workout != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int type = 0;
                    if(which == DialogInterface.BUTTON_NEGATIVE) type = 1;

                    workout.setType(type);
                    log_ui_manager.update_CurrentType(type);

                    wrapper.open();
                    wrapper.updateWorkout(workout);
                    wrapper.close();
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DarkAlertDialog);
            builder.setMessage("Choose workout type")
                    .setTitle("Change Workout Type")
                    .setPositiveButton("Simple", dialogClickListener)
                    .setNegativeButton("Mix", dialogClickListener)
                    .show();
        }
        else{
            showDarkToast("First Start Workout");
        }
    }

    public void update_MaxValue() {
        if(workout != null) {
            Intent intent = new Intent(context, WorkoutMaximumActivity.class);

            intent.putExtra(Constants.WORKOUT_TYPE_KEY, workout.getType());
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, chosenWorkout);
            intent.putExtra(Constants.UPDATING_MAX_IN_SETTINGS, true);

            context.startActivity(intent);
        }
        else{
            showDarkToast("First Start Workout");
        }
    }
    
    /**
     * Show a toast message with proper dark theme styling
     */
    private void showDarkToast(String message) {
        // Try to use Snackbar for better theming support
        if (context instanceof android.app.Activity) {
            View rootView = ((android.app.Activity) context).findViewById(android.R.id.content);
            if (rootView != null) {
                Snackbar snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT);
                // Style the snackbar with dark colors
                snackbar.setActionTextColor(context.getResources().getColor(R.color.colorAccent));
                View snackbarView = snackbar.getView();
                snackbarView.setBackgroundColor(context.getResources().getColor(R.color.background_elevated));
                snackbar.show();
                return;
            }
        }
        // Fallback to regular toast if snackbar fails
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
