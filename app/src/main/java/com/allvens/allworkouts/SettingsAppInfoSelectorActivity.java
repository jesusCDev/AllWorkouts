package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;

public class SettingsAppInfoSelectorActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app_info_selector);
    }

    public void btnAction_ShowDocumentation(View view){
        Intent intent = new Intent(this, SettingsAppInfoPresenterActivity.class);
        String value  = view.getId() == R.id.btn_settings_appInfo_selector_openSource
                ? Constants.OPEN_SOURCE
                : Constants.TERMS_OF_USE;

        intent.putExtra(Constants.CHOSEN_DOCUMENTATION, value);
        startActivity(intent);
    }
}
