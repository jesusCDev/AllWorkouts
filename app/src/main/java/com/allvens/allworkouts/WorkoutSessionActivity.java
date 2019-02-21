package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.workout_session_manager.WorkoutSession_Manager;

public class WorkoutSessionActivity extends AppCompatActivity {

    private WorkoutSession_Manager manager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_session);

        TextView tv_workout_WorkoutName = findViewById(R.id.tv_workout_WorkoutName);
        LinearLayout llTimeImageHolder = findViewById(R.id.ll_workout_timeImageHolder);
        LinearLayout llWorkoutHelper = findViewById(R.id.ll_workout_workout_helper);

        TextView tvValue1 = findViewById(R.id.tv_workout_Value1);
        TextView tvValue2 = findViewById(R.id.tv_workout_Value2);
        TextView tvValue3 = findViewById(R.id.tv_workout_Value3);
        TextView tvValue4 = findViewById(R.id.tv_workout_Value4);
        TextView tvValue5 = findViewById(R.id.tv_workout_Value5);

        Button btn_ChangeScreens = findViewById(R.id.btn_workout_CompleteTask);
        ImageButton btn_WorkoutHelper = findViewById(R.id.btn_workout_WorkoutHelper);

        manager = new WorkoutSession_Manager(this, getIntent().getExtras().get(Constants.CHOSEN_WORKOUT_EXTRA_KEY).toString());
        manager.setUp_UiManager(tv_workout_WorkoutName, llTimeImageHolder, llWorkoutHelper, tvValue1,
                tvValue2, tvValue3, tvValue4, tvValue5, btn_ChangeScreens, btn_WorkoutHelper);
        manager.set_Timer();
        manager.start_Screen();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        manager.kill_Timer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /****************************************
     /**** BUTTON ACTIONS
     ****************************************/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        manager.kill_Timer();
        startActivity(new Intent(this, MainActivity.class));
        return true;
    }

    public void btnAction_OpenCloseWorkoutHelper(View view){
        manager.clear_WorkoutHelper();
        if(!manager.get_WorkoutHelperOpen()){
            manager.open_WorkoutHelper();
        }
        manager.set_WorkoutHelperOpen(!manager.get_WorkoutHelperOpen());
    }

    /**
     * Switches Screen between - Workout and Timer
     * @param view
     */
    public void btnAction_ChangeActivities(View view){
        manager.clear_WorkoutHelper();
        manager.set_WorkoutHelperOpen(false);

        if(manager.check_IfFinished()){
            manager.update_Screen();
        }else{
            Intent intent = new Intent(this, WorkoutSessionFinishActivity.class);
            intent.putExtra(Constants.CHOSEN_WORKOUT_EXTRA_KEY, manager.get_Workout());
            this.startActivity(intent);
            finish();
        }

    }
}
