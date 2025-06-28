package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;

import java.util.ArrayList;

public class textDocumentationManager {

    private Context context;
    private ArrayList<View> views = new ArrayList<>();

    public textDocumentationManager(Context context){
        this.context = context;
    }

    private void createTextView(String text, int resourceId) {
        TextView textView = new TextView(context);

        textView.setText(text);

        if(Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, resourceId);
        }
        else {
            textView.setTextAppearance(resourceId);
        }

        this.views.add(textView);
        addSpace();
    }

    public void createTitle(int resourceID){
        createTextView(getTextFromR(resourceID), R.style.textStyle_Doc_Title);
    }

    public void createSubTitle(int resourceID){
        this.createSubTitle(getTextFromR(resourceID));
    }

    public void createSubTitle(String text){
        createTextView(text, R.style.textStyle_Doc_SubTitle);
    }

    public void createParagraph(String text){
        createTextView(text, R.style.textStyle_Doc_Paragraph);
    }

    public void createParagraph(int resourceID){
        this.createParagraph(getTextFromR(resourceID));
    }

    private void addSpace() {
        TextView tvSpace = new TextView(context);

        tvSpace.setText(" ");
        views.add(tvSpace);
    }

    public String getTextFromR(int resourceID){
        return context.getResources().getString(resourceID);
    }

    public void showViews(LinearLayout llContainer){
        for(View view: views) {
            llContainer.addView(view);
        }
    }
}
