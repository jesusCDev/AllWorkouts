package com.allvens.allworkouts.settings_manager.Notification_Manager;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;

import java.util.Calendar;

public class NotificationReceiver extends BroadcastReceiver {
    private int MID                     = 100;
    private String ANDROID_CHANNEL_ID   = "com.android.AllWorkouts";
    private String ANDROID_CHANNEL_NAME = "All Workouts";
    private String NOTIFICATION_MESSAGE = "Ready To Workout!";

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsPrefsManager settingsPrefs = new SettingsPrefsManager(context);

        if(intent.getAction() != null && context != null) {
            if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Create Notification Receiver
                WorkoutNotificationManager notiManager = new WorkoutNotificationManager(context, settingsPrefs.getPrefSetting(PreferencesValues.NOTIFICATION_ON),
                                settingsPrefs.get_NotifiHour(), settingsPrefs.get_NotifiMinute());

                notiManager.create_Notification();
            }
        }

        // If this is a test notification, post immediately and return
        if (intent.getBooleanExtra("is_test", false)) {
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);
            Notification.Builder mNotifyBuilder = create_Notification(context);
            notificationManager.notify(MID, mNotifyBuilder.build());
            return;
        }

        Calendar rightNow = Calendar.getInstance();
        int rightNowHour  = rightNow.get(Calendar.HOUR_OF_DAY);
        int rightNowMin   = rightNow.get(Calendar.MINUTE);

        if(settingsPrefs.get_NotificationDayValue((Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1)) && (rightNowHour == settingsPrefs.get_NotifiHour()) && (rightNowMin == settingsPrefs.get_NotifiMinute())){
            NotificationManager notificationManager = (NotificationManager) context
                    .getSystemService(Context.NOTIFICATION_SERVICE);

            Notification.Builder mNotifyBuilder = create_Notification(context);
            notificationManager.notify(MID, mNotifyBuilder.build());
        }
    }

    private Notification.Builder create_Notification(Context context) {
        Intent notificationIntent = new Intent(context, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            flags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(context, MID, notificationIntent, flags);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Detect if system is in dark mode
            boolean isDarkMode = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
            
            Notification.Builder builder = new Notification.Builder(context.getApplicationContext(), ANDROID_CHANNEL_ID)
                    .setContentTitle(ANDROID_CHANNEL_NAME)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_pullup)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setCategory(Notification.CATEGORY_REMINDER);
            
            // Set color with better dark/light theme handling
            // Use a brighter color for dark theme visibility
            int notificationColor;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationColor = isDarkMode 
                    ? context.getResources().getColor(R.color.notification_primary, null) 
                    : context.getResources().getColor(R.color.primary, null);
                builder.setColor(notificationColor);
            } else {
                notificationColor = context.getResources().getColor(R.color.primary);
                builder.setColor(notificationColor);
            }
            
            // For dark theme, make notification more prominent
            if (isDarkMode && Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                builder.setColorized(true); // This makes the notification use our color more prominently
            }
            
            return builder;
        }
        else{
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long when      = System.currentTimeMillis();

            Notification.Builder builder = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.ic_pullup)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle(ANDROID_CHANNEL_NAME)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setSound(alarmSound)
                    .setAutoCancel(true)
                    .setWhen(when)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
                    
            // Set color for legacy versions - use notification specific color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                builder.setColor(context.getResources().getColor(R.color.notification_primary, null));
            } else {
                builder.setColor(context.getResources().getColor(R.color.primary));
            }
            
            return builder;
        }
    }
}
