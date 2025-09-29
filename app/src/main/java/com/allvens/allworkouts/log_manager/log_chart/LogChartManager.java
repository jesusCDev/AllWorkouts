package com.allvens.allworkouts.log_manager.log_chart;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.allvens.allworkouts.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * Open Source Project - https://github.com/PhilJay/MPAndroidChart
 * Used to create chart to represent data
 */
public class LogChartManager {

    private LineChart lc;
    private Context context;

    public LogChartManager(Context context, LineChart lc) {
        this.context = context;
        this.lc = lc;
    }

    public void setUp_ChartValues() {
        lc.setDragEnabled(false);
        lc.setScaleEnabled(false);
        lc.getAxisRight().setEnabled(false);
    }

    /********** Line Chart Methods **********/

    public void create_Chart(ArrayList<LineChartDataEntry> totalSets){

        // Configure chart styling for dark theme
        lc.getXAxis().setDrawLabels(true);
        lc.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.selectedButton));
        lc.getXAxis().setGridColor(ContextCompat.getColor(context, R.color.unSelectedButton));
        
        lc.getAxisLeft().setTextColor(ContextCompat.getColor(context, R.color.selectedButton));
        lc.getAxisLeft().setGridColor(ContextCompat.getColor(context, R.color.unSelectedButton));
        
        lc.getLegend().setTextColor(ContextCompat.getColor(context, R.color.selectedButton));
        lc.getDescription().setEnabled(false);
        
        // Set dark background for the chart
        lc.setBackgroundColor(ContextCompat.getColor(context, R.color.background_elevated));
        
        ArrayList<Entry> yValues = create_Entries(totalSets);
        LineDataSet set = new LineDataSet(yValues, "Progress (Last 21)");
        
        // Style the data points and line for better visibility
        set.setCircleColor(ContextCompat.getColor(context, R.color.colorAccent));
        set.setCircleHoleColor(ContextCompat.getColor(context, R.color.background_elevated));
        set.setValueTextColor(ContextCompat.getColor(context, R.color.selectedButton));
        set.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        set.setLineWidth(3f);
        set.setCircleRadius(4f);
        set.setValueTextSize(10f);
        set.setDrawFilled(true);
        set.setFillAlpha(30);
        set.setFillColor(ContextCompat.getColor(context, R.color.colorAccent));

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(dataSets);

        lc.setData(data);
    }

    private ArrayList<Entry> create_Entries(ArrayList<LineChartDataEntry> currentWeekData){
        ArrayList<Entry> yValues = new ArrayList<>();
        for(LineChartDataEntry entry: currentWeekData){
            yValues.add(new Entry(entry.getPosition(), entry.getValue()));
        }
        return yValues;
    }

    public void reset_Chart(){
        lc.invalidate();
        lc.clear();
    }
}