package com.allvens.allworkouts;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.PreferencesValues;

public class StartingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivyt_starting);
    }

    public void btnAction_StartApp(View view) {

        SharedPreferences.Editor editor = getSharedPreferences(PreferencesValues.PREFS_NAMES, MODE_PRIVATE).edit();
        editor.putBoolean(PreferencesValues.FIRST_TIME_USING, false);
        editor.commit();

        startActivity(new Intent(this, HomeActivity.class));
    }
}
