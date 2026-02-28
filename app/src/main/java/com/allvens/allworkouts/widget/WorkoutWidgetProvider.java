package com.allvens.allworkouts.widget;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.R;

import java.util.Calendar;

public class WorkoutWidgetProvider extends AppWidgetProvider {

    private static final String ACTION_WIDGET_UPDATE =
            "com.allvens.allworkouts.ACTION_WIDGET_UPDATE";

    private static final int TODAY_INDEX = 4;

    private static final int[] CELL_IDS = {
            R.id.widget_cell_0,
            R.id.widget_cell_1,
            R.id.widget_cell_2,
            R.id.widget_cell_3,
            R.id.widget_cell_4,
            R.id.widget_cell_5,
            R.id.widget_cell_6,
            R.id.widget_cell_7,
            R.id.widget_cell_8
    };

    /** Regular (non-today) cell drawables by intensity level 0-4. */
    private static final int[] CELL_DRAWABLES = {
            R.drawable.bg_widget_cell_empty,
            R.drawable.bg_widget_cell_low,
            R.drawable.bg_widget_cell_medium,
            R.drawable.bg_widget_cell_high,
            R.drawable.bg_widget_cell_max
    };

    /** Today cell drawables by intensity level 0-4 (with bone stroke). */
    private static final int[] CELL_TODAY_DRAWABLES = {
            R.drawable.bg_widget_cell_today_empty,
            R.drawable.bg_widget_cell_today_low,
            R.drawable.bg_widget_cell_today_medium,
            R.drawable.bg_widget_cell_today_high,
            R.drawable.bg_widget_cell_today_max
    };

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (ACTION_WIDGET_UPDATE.equals(intent.getAction())) {
            AppWidgetManager mgr = AppWidgetManager.getInstance(context);
            int[] ids = mgr.getAppWidgetIds(
                    new ComponentName(context, WorkoutWidgetProvider.class));
            onUpdate(context, mgr, ids);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        scheduleMidnightAlarm(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        cancelMidnightAlarm(context);
    }

    private void updateWidget(Context context, AppWidgetManager mgr, int appWidgetId) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_workout);

        WidgetDataHelper.WidgetData data = WidgetDataHelper.gatherWidgetData(context);

        // --- Streak ---
        views.setTextViewText(R.id.widget_streak, String.valueOf(data.streak));
        views.setTextViewText(R.id.widget_streak_label, data.streak == 1 ? "day" : "days");

        // --- 9 day cells ---
        for (int i = 0; i < 9; i++) {
            int intensity = intensityLevel(data.dayCounts[i]);
            int drawable;
            if (i == TODAY_INDEX && data.isMaxDay[i]) {
                // Today is a max day
                drawable = R.drawable.bg_widget_cell_today_maxday;
            } else if (i == TODAY_INDEX) {
                drawable = CELL_TODAY_DRAWABLES[intensity];
            } else if (i > TODAY_INDEX && data.isMaxDay[i]) {
                // Future cell predicted as max day
                drawable = R.drawable.bg_widget_cell_maxday;
            } else {
                drawable = CELL_DRAWABLES[intensity];
            }
            views.setImageViewResource(CELL_IDS[i], drawable);
        }

        // --- Click intent to open MainActivity ---
        Intent launchIntent = new Intent(context, MainActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_root, pendingIntent);

        mgr.updateAppWidget(appWidgetId, views);
    }

    /** Maps workout count to intensity level 0-4, matching the calendar heat-map. */
    private static int intensityLevel(int count) {
        if (count <= 0) return 0;
        if (count == 1) return 1;
        if (count == 2) return 2;
        if (count == 3) return 3;
        return 4;
    }

    /** Static helper to request a widget update from anywhere in the app. */
    public static void requestUpdate(Context context) {
        Intent intent = new Intent(context, WorkoutWidgetProvider.class);
        intent.setAction(ACTION_WIDGET_UPDATE);
        context.sendBroadcast(intent);
    }

    private void scheduleMidnightAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, WidgetMidnightReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Calendar midnight = Calendar.getInstance();
        midnight.set(Calendar.HOUR_OF_DAY, 0);
        midnight.set(Calendar.MINUTE, 0);
        midnight.set(Calendar.SECOND, 0);
        midnight.set(Calendar.MILLISECOND, 0);
        midnight.add(Calendar.DAY_OF_MONTH, 1);

        alarmManager.set(AlarmManager.RTC, midnight.getTimeInMillis(), pi);
    }

    private void cancelMidnightAlarm(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager == null) return;

        Intent intent = new Intent(context, WidgetMidnightReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        alarmManager.cancel(pi);
    }
}
