package com.allvens.allworkouts.assets;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class Permission_Checker implements ActivityCompat.OnRequestPermissionsResultCallback {

    public static final String PERMISSION_SET_ALARM = Manifest.permission.SET_ALARM;
    public static final int PERMISSION_SET_ALARM_REQUEST_CODE = 46;
    public static final String PERMISSION_VIBRATE = Manifest.permission.VIBRATE;
    public static final int PERMISSION_VIBRATE_REQUEST_CODE = 56;
    public static final String PERMISSION_REIEVE_BOOT_COMPLETED = Manifest.permission.RECEIVE_BOOT_COMPLETED;
    public static final int PERMISSION_REICEVE_BOOT_COMPLETED_REQUEST_CODE = 66;
    public static final String PERMISSION_MODIFY_AUDIO_SETTINGS = Manifest.permission.MODIFY_AUDIO_SETTINGS;
    public static final int PERMISSION_MODIFY_AUDIO_SETTINGS_REQUEST_CODE = 76;

    private Context context;
    public Permission_Checker(Context context){
        this.context = context;
    }

    public boolean check_AndroidSupportPermissionRequest(){
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }

    public boolean check_Permission(String permission){
        return (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED);
    }

    public void request_Permission(String permission, int requestCode){
        ActivityCompat.requestPermissions((AppCompatActivity)context, new String[]{permission}, requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_SET_ALARM_REQUEST_CODE:
                if (grantResults.length > 0 // request empty if cancelled
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
            break;
            case PERMISSION_VIBRATE_REQUEST_CODE:
                if (grantResults.length > 0 // request empty if cancelled
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_REICEVE_BOOT_COMPLETED_REQUEST_CODE:
                if (grantResults.length > 0 // request empty if cancelled
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
            case PERMISSION_MODIFY_AUDIO_SETTINGS_REQUEST_CODE:
                if (grantResults.length > 0 // request empty if cancelled
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
