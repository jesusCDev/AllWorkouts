package com.allvens.allworkouts.settings_manager.Notification_Manager;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import java.util.Calendar;

import com.allvens.allworkouts.R;

import static android.content.Context.ALARM_SERVICE;

public class WorkoutNotificationManager {

    private int hour;
    private int minute;
    private boolean notificationOn;
    private Context context;

    public WorkoutNotificationManager(Context context, boolean notificationOn, int hour, int minute){
        this.context        = context;
        this.notificationOn = notificationOn;
        this.hour           = hour;
        this.minute         = minute;
    }

    /****************************************
     /**** SETTER/GETTER METHODS
     ****************************************/

    public void set_NotificationOn(boolean value){
        notificationOn = value;
    }

    public void set_Time(int hour, int min) {
        this.hour   = hour;
        this.minute = min;
    }

    public int get_Hour(){
        return hour;
    }

    public int get_Min(){
        return minute;
    }

    public void update_Time(int hour, int minute){
        this.hour   = hour;
        this.minute = minute;

        if(notificationOn){
            cancel_Notification();
            create_Notification();
        }
    }

    private android.app.NotificationManager getManager() {
        if(mManager == null) {
            mManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    private android.app.NotificationManager mManager;
    private String ANDROID_CHANNEL_ID   = "com.android.AllWorkouts";
    private String ANDROID_CHANNEL_NAME = "All Workouts";
    private int MID                     = 100;

    public void create_Notification(){
        try {
            cancel_Notification();

            if(Build.VERSION.SDK_INT >= 26) {
                NotificationChannel androidChannel = new NotificationChannel(ANDROID_CHANNEL_ID,
                        ANDROID_CHANNEL_NAME, android.app.NotificationManager.IMPORTANCE_DEFAULT);

                androidChannel.setDescription("Workout reminder notifications");
                androidChannel.enableLights(true);
                androidChannel.enableVibration(true);
                // Use app's notification color for LED light
                androidChannel.setLightColor(context.getResources().getColor(R.color.notification_primary));
                androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
                androidChannel.setShowBadge(true);
                getManager().createNotificationChannel(androidChannel);
            }

            Calendar calendar = Calendar.getInstance();

            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            Intent intent1              = new Intent(context, NotificationReceiver.class);
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, MID, intent1, flags);
            AlarmManager am             = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

            ComponentName receiver = new ComponentName(context   , NotificationReceiver.class);
            PackageManager pm      = context.getPackageManager();

            pm.setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        } catch (Exception e) {
            // Log error or handle gracefully - don't crash the app
            e.printStackTrace();
        }
    }

    public void cancel_Notification(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            android.app.NotificationManager mNotificationManager =
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            mNotificationManager.deleteNotificationChannel(ANDROID_CHANNEL_ID);
        }
        else{
            Intent intent             = new Intent(context, NotificationReceiver.class);
            int flags = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                flags |= PendingIntent.FLAG_IMMUTABLE;
            }
            PendingIntent sender      = PendingIntent.getBroadcast(context, MID, intent, flags);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            alarmManager.cancel(sender);
        }

        ComponentName receiver = new ComponentName(context   , NotificationReceiver.class);
        PackageManager pm      = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }
}
