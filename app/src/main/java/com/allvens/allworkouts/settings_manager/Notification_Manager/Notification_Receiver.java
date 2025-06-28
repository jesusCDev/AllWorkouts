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

import com.allvens.allworkouts.MainActivity;
import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.Preferences_Values;
import com.allvens.allworkouts.settings_manager.SettingsPrefs_Manager;

import java.util.Calendar;

public class Notification_Receiver extends BroadcastReceiver {
    private int MID                     = 100;
    private String ANDROID_CHANNEL_ID   = "com.android.AllWorkouts";
    private String ANDROID_CHANNEL_NAME = "All Workouts";
    private String NOTIFICATION_MESSAGE = "Ready To Workout!";

    @Override
    public void onReceive(Context context, Intent intent) {
        SettingsPrefs_Manager settingsPrefs = new SettingsPrefs_Manager(context);

        if(intent.getAction() != null && context != null) {
            if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Create Notification Receiver
                Notification_Manager notiManager = new Notification_Manager(context, settingsPrefs.get_PrefSetting(Preferences_Values.NOTIFICATION_ON),
                                settingsPrefs.get_NotifiHour(), settingsPrefs.get_NotifiMinute());

                notiManager.create_Notification();
            }
        }

        Calendar rightNow = Calendar.getInstance();
        int rightNowHour  = rightNow.get(Calendar.HOUR);
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

        PendingIntent pendingIntent = PendingIntent.getActivity(context, MID,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return new Notification.Builder(context.getApplicationContext(), ANDROID_CHANNEL_ID)
                    .setContentTitle(ANDROID_CHANNEL_NAME)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setContentIntent(pendingIntent)
                    .setSmallIcon(R.drawable.ic_pullup)
                    .setAutoCancel(true);
        }
        else{
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            long when      = System.currentTimeMillis();

            return new Notification.Builder(
                    context).setSmallIcon(R.drawable.ic_pullup)
                    .setContentTitle(ANDROID_CHANNEL_NAME)
                    .setContentText(NOTIFICATION_MESSAGE)
                    .setSound(alarmSound)
                    .setAutoCancel(true).setWhen(when)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000});
        }
    }
}
