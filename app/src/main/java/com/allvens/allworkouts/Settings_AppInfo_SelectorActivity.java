package com.allvens.allworkouts;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.allvens.allworkouts.assets.Constants;

public class Settings_AppInfo_SelectorActivity extends AppCompatActivity{

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app_info_selector);
    }

    public void btnAction_ShowDocumentation(View view){
        String value;

        switch (view.getId()){
            case R.id.btn_settings_appInfo_selector_openSource:
                value = Constants.OPEN_SOURCE;
                break;

            default:
            case R.id.btn_settings_appInfo_selector_termsOfUse:
                value = Constants.TERMS_OF_USE;
                break;
        }

        Intent intent = new Intent(this, Settings_AppInfo_PresenterActivity.class);

        intent.putExtra(Constants.CHOSEN_DOCUMENTATION, value);
        startActivity(intent);
    }
}
