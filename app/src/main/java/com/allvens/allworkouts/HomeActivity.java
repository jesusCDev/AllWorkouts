package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.database.Workout_Info;
import com.allvens.allworkouts.database.Workout_Wrapper;

public class HomeActivity extends AppCompatActivity {


    private LinearLayout ll_home_WorkoutChooser;
    private TextView tv_CurrentWorkout;

    private boolean workoutChooserTracker = false;
    private static String choice = "Pull Ups";
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
                    btn_PullUps.setOnClickListener(changeWorkout("Pull Ups"));
                    set_BtnChoiceStyle(btn_PullUps, R.style.btn_home_workoutChoice, "Pull Ups");
                    Button btn_PushUps = new Button(HomeActivity.this);
                    btn_PushUps.setOnClickListener(changeWorkout("Push Ups"));
                    set_BtnChoiceStyle(btn_PushUps, R.style.btn_home_workoutChoice, "Push Ups");
                    Button btn_SitUps = new Button(HomeActivity.this);
                    btn_SitUps.setOnClickListener(changeWorkout("Sit Ups"));
                    set_BtnChoiceStyle(btn_SitUps, R.style.btn_home_workoutChoice, "Sit Ups");
                    Button btn_Squats = new Button(HomeActivity.this);
                    btn_Squats.setOnClickListener(changeWorkout("Squats"));
                    set_BtnChoiceStyle(btn_Squats, R.style.btn_home_workoutChoice, "Squats");

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
                btn_ChangeWorkouts.setText("^ " + choice);
                workoutChooserTracker = false;
                ll_home_WorkoutChooser.removeAllViews();
            }
        });
    }



    private void set_BtnChoiceStyle(Button btn, int style, String btnText){

        tv_CurrentWorkout.setText(btnText);
        btn.setText(btnText);

        if (Build.VERSION.SDK_INT < 23) {
            btn.setTextAppearance(HomeActivity.this, style);
        } else {
            btn.setTextAppearance(style);
        }
    }

    public void btnAction_StartWorkout(View view) {

        // Every 7 workouts add a new maximum
        // Check if you have set a new maximum
        // Check if they are doing simple or mix

        Intent intent;
        if(check_WorkoutExist()){
            if(check_WorkoutProgress()){
                intent = new Intent(HomeActivity.this, WorkoutActivity.class);
            }else{
                intent = new Intent(HomeActivity.this, MaximumActivity.class);
            }
        }else{
            create_Workout();
            intent = new Intent(HomeActivity.this, MaximumActivity.class);
        }
        intent.putExtra("chosenWorkout", choice);
        startActivity(intent);
    }

    private boolean check_WorkoutExist(){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choice)) {
                return true;
            }
        }
        return false;
    }
    private void create_Workout(){
        // tell user to pick simple or mix
        // create workout
        // start maximum
    }

    private boolean check_WorkoutProgress(){
        for(Workout_Info workout: wrapper.get_AllWorkouts()){
            if (workout.getWorkout().equalsIgnoreCase(choice)) {
                return (workout.getProgress() < 7);
            }
        }
        return false;
    }

    private boolean checkIfLast7RoundsMax(){
        return true;
    }

    public void btnAction_Settings(View view) {
    }

    public void btnAction_Log(View view) {
    }
}
