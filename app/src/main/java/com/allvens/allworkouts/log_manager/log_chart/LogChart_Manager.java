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

public class LogChart_Manager {

    private LineChart lc;
    private Context context;

    public LogChart_Manager(Context context, LineChart lc) {
        this.context = context;
        this.lc = lc;
    }

    public void reset_Chart(){
        lc.invalidate();
        lc.clear();
    }

    /**
     * Sets up basic chart values
     */
    public void setUp_ChartValues() {
        lc.setDragEnabled(false);
        lc.setScaleEnabled(false);
        lc.getAxisRight().setEnabled(false);
    }

    public void create_Chart(ArrayList<LogData_Entry> currentWeekData){

        lc.getXAxis().setDrawLabels(false);
        lc.getXAxis().setTextColor(ContextCompat.getColor(context, R.color.objects));
        lc.getAxisLeft().setTextColor(ContextCompat.getColor(context, R.color.objects));

        lc.getLegend().setTextColor(ContextCompat.getColor(context, R.color.objects));
        lc.getDescription().setEnabled(false);

        ArrayList<Entry> yValues = create_Entries(currentWeekData);
        LineDataSet set = new LineDataSet(yValues, "Last 20 Max");
        set.setCircleColor(ContextCompat.getColor(context, R.color.objects));
        set.setValueTextColor(ContextCompat.getColor(context, R.color.objects));
        set.setFillAlpha(110);
        set.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        set.setLineWidth(3f);
        set.setValueTextSize(10f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set);

        LineData data = new LineData(dataSets);

        lc.setData(data);
    }

    /**
     * Creates Entries for chart
     * @param currentWeekData
     * @return Entry List
     */
    private ArrayList<Entry> create_Entries(ArrayList<LogData_Entry> currentWeekData){
        ArrayList<Entry> yValues = new ArrayList<>();
        for(LogData_Entry entry: currentWeekData){
            yValues.add(new Entry(entry.getPosition(), entry.getValue()));
        }
        return yValues;
    }
}