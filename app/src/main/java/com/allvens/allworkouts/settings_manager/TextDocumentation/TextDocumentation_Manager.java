package com.allvens.allworkouts.settings_manager.TextDocumentation;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.allvens.allworkouts.R;

import java.util.ArrayList;

public class TextDocumentation_Manager {

    private Context context;
    private ArrayList<View> views = new ArrayList<>();

    public TextDocumentation_Manager(Context context){
        this.context = context;
    }

    public void create_Title(int resourceID){
        TextView tvText = new TextView(context);
        tvText.setText(getTextFromR(resourceID));
        set_Style(tvText, R.style.textStyle_Doc_Title);
        views.add(tvText);
        add_Space();
    }

    public void create_SubTitle(String text){
        TextView tvText = new TextView(context);
        tvText.setText(text);
        set_Style(tvText, R.style.textStyle_Doc_SubTitle);
        views.add(tvText);
        add_Space();
    }

    public void create_Paragraph(String text){
        TextView tvText = new TextView(context);
        tvText.setText(text);
        set_Style(tvText, R.style.textStyle_Doc_Paragraph);
        views.add(tvText);
        add_Space();
    }

    public void create_SubTitle(int resourceID){
        create_SubTitle(getTextFromR(resourceID));
    }

    public void create_Paragraph(int resourceID){
        create_Paragraph(getTextFromR(resourceID));
    }

    public OrderList create_OrderList(){
        return new OrderList();
    }


    private void set_Style(TextView textView, int resourceID){
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(context, resourceID);
        } else {
            textView.setTextAppearance(resourceID);
        }
    }

    private void add_Space() {
        TextView tvSpace = new TextView(context);
        tvSpace.setText(" ");
        views.add(tvSpace);
    }

    public String getTextFromR(int resourceID){
        return context.getResources().getString(resourceID);
    }

    public void show_Views(LinearLayout llContainer){
        for(View view: views){
            llContainer.addView(view);
        }
    }
}
