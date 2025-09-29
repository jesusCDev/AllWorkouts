package com.allvens.allworkouts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.allvens.allworkouts.assets.Constants;
import com.allvens.allworkouts.settings_manager.TextDocumentation.TextDocumentationOpenSource;
import com.allvens.allworkouts.settings_manager.TextDocumentation.TextDocumentationTermsOfService;

public class SettingsAppInfoPresenterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_app_info_presenter);

        LinearLayout llContainer = findViewById(R.id.ll_settings_appInfo_Container);

        switch(getIntent().getStringExtra(Constants.CHOSEN_DOCUMENTATION)) {
            case Constants.OPEN_SOURCE:
                new TextDocumentationOpenSource(this).showViews(llContainer);
                break;

            case Constants.TERMS_OF_USE:
                new TextDocumentationTermsOfService(this).showViews(llContainer);
                break;
        }
    }
}
