package com.allvens.allworkouts.settings_manager.Notification_Manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

public class Notification_Controller {

    private int hour;
    private int minute;
    private boolean notificationOn;
    private Context context;

    public Notification_Controller(Context context, boolean notificationOn, int hour, int minute){
        this.context = context;
        this.notificationOn = notificationOn;
        this.hour = hour;
        this.minute = minute;
    }

    public void set_NotificationOn(boolean value){
        notificationOn = value;
    }

    public int get_Hour(){
        return hour;
    }

    public int get_Min(){
        return minute;
    }

    public void update_Time(int hour, int minute){
        this.hour = hour;
        this.minute = minute;

        if(notificationOn){
            cancel_Notification();
            create_Notification();
        }
    }

    private NotificationManager mManager;
    private String ANDROID_CHANNEL_ID = "com.android.AllWorkouts";
    private String ANDROID_CHANNEL_NAME = "All Workouts";
    private int MID = 100;

    public void create_Notification(){

        cancel_Notification();

        if (Build.VERSION.SDK_INT >= 26) {
            NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                    ANDROID_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            androidChannel.enableLights(true);
            androidChannel.enableVibration(true);
            androidChannel.setLightColor(Color.GREEN);
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            getManager().createNotificationChannel(androidChannel);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        Intent intent1 = new Intent(context, Notification_Receiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MID,intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

        ComponentName receiver = new ComponentName(context, Notification_Receiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    private NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public void cancel_Notification(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.deleteNotificationChannel(ANDROID_CHANNEL_ID);
        }else{
            Intent intent = new Intent(context, Notification_Receiver.class);
            PendingIntent sender = PendingIntent.getBroadcast(context, MID, intent, 0);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            alarmManager.cancel(sender);
        }

        ComponentName receiver = new ComponentName(context, Notification_Receiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public void set_Time(int hour, int min) {
        this.hour = hour;
        this.minute = min;
    }
}
