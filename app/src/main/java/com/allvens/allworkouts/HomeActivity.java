package com.allvens.allworkouts;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class HomeActivity extends AppCompatActivity {


    private LinearLayout ll_home_WorkoutChooser;
    private TextView tv_CurrentWorkout;

    private boolean workoutChooserTracker = false;
    private static String choice = Constants.PULL_UPS;
    private Button btn_ChangeWorkouts;

    private Workout_Wrapper wrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        tv_CurrentWorkout = findViewById(R.id.tv_home_CurrentWorkout);
        btn_ChangeWorkouts = findViewById(R.id.btn_ChangeWorkouts);
        btn_ChangeWorkouts.setOnClickListener(btnAction_changeWorkouts());
        ll_home_WorkoutChooser = findViewById(R.id.ll_home_WorkoutChooser);

        wrapper = new Workout_Wrapper(this);
        wrapper.open();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wrapper.open();
    }

    @Override
    protected void onStop() {
        super.onStop();
        wrapper.close();
    }

    private View.OnClickListener btnAction_changeWorkouts() {
        return (new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ll_home_WorkoutChooser.removeAllViews();

                if(!workoutChooserTracker){
                    Button btn_PullUps = new Button(HomeActivity.this);
                    btn_PullUps.setOnClickListener(changeWorkout(Constants.PULL_UPS));
                    set_BtnChoiceStyle(btn_PullUps, R.style.btn_home_workoutChoice, Constants.PULL_UPS);
                    Button btn_PushUps = new Button(HomeActivity.this);
                    btn_PushUps.setOnClickListener(changeWorkout(Constants.PUSH_UPS));
                    set_BtnChoiceStyle(btn_PushUps, R.style.btn_home_workoutChoice, Constants.PUSH_UPS);
                    Button btn_SitUps = new Button(HomeActivity.this);
                    btn_SitUps.setOnClickListener(changeWorkout(Constants.SIT_UPS));
                    set_BtnChoiceStyle(btn_SitUps, R.style.btn_home_workoutChoice, Constants.SIT_UPS);
                    Button btn_Squats = new Button(HomeActivity.this);
                    btn_Squats.setOnClickListener(changeWorkout(Constants.SQUATS));
                    set_BtnChoiceStyle(btn_Squats, R.style.btn_home_workoutChoice, Constants.SQUATS);

                    ll_home_WorkoutChooser.addView(btn_PullUps);
                    ll_home_WorkoutChooser.addView(btn_PushUps);
                    ll_home_WorkoutChooser.addView(btn_SitUps);
                    ll_home_WorkoutChooser.addView(btn_Squats);
                }

                workoutChooserTracker = !workoutChooserTracker;
            }
        });
    }

    private View.OnClickListener changeWorkout(final String choice){

        return (new View.OnClickListener(){
            @Override
            public void onClick(View v){

                HomeActivity.choice = choice;
                tv_CurrentWorkout.setText(choice);
                btn_ChangeWorkouts.setText("^ " + choice);
                workoutChooserTracker = false;
                ll_home_WorkoutChooser.removeAllViews();
            }
        });
    }



    private void set_BtnChoiceStyle(Button btn, int style, String btnText){

        btn.setText(btnText);
        if (Build.VERSION.SDK_INT < 23) {
            btn.setTextAppearance(HomeActivity.this, style);
        } else {
            btn.setTextAppearance(style);
        }
    }

    public void btnAction_StartWorkout(View view) {
        Intent intent;
        if(check_WorkoutExist()){
            if(check_WorkoutProgress()){
                intent = new Intent(HomeActivity.this, WorkoutActivity.class);
            }else{
                intent = new Intent(HomeActivity.this, WorkoutMaximumActivity.class);
            }

            intent.putExtra(Constants.CHOOSEN_WORKOUT_EXTRA_KEY, choice);
            startActivity(intent);
        }else{
            start_newSession();
        }
    }

    private boolean check_WorkoutExist(){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choice)) {
                    Log.d("Bug", "Progress: " + workout.getProgress());
                if(workout.getProgress() != 0){
                    return true;
                }else{
                    wrapper.delete_Workout(workout);
                }
            }
        }
        return false;
    }

    private void create_Workout(int workoutType){
        // create workout
        wrapper.create_Workout(new Workout_Info(choice, 0, workoutType, 0));
        wrapper.close();

        Intent intent = new Intent(HomeActivity.this, WorkoutMaximumActivity.class);

        intent.putExtra(Constants.CHOOSEN_WORKOUT_EXTRA_KEY, choice);
        startActivity(intent);
    }

    private void start_newSession(){
        final String[] workoutTypes = {"Simple", "Mix"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick workout type.");
        builder.setItems(workoutTypes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                create_Workout(which);
            }
        });
        builder.show();
    }

    private boolean check_WorkoutProgress(){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choice)) {
                Log.d("Bug", "Checking; " + workout.getProgress());
                return (workout.getProgress() < 8);
            }
        }
        return false;
    }

    public void btnAction_Settings(View view) {
    }

    public void btnAction_Log(View view) {
    }
}
