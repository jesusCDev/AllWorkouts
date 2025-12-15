package com.allvens.allworkouts.media;

import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import java.util.List;

/**
 * NotificationListenerService to capture media session information from music apps
 * This service requires the user to grant notification access permission in Android Settings
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MediaNotificationListenerService extends NotificationListenerService {
    
    private static final String TAG = "MediaNotifListener";
    private MediaSessionManager mediaSessionManager;
    private MediaSessionManager.OnActiveSessionsChangedListener sessionsChangedListener;
    
    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(TAG, "Service created");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            setupSessionListener();
        }
    }
    
    /**
     * Set up listener for active media session changes
     */
    private void setupSessionListener() {
        if (mediaSessionManager == null) return;
        
        try {
            sessionsChangedListener = controllers -> {
                android.util.Log.d(TAG, "Active sessions changed, count: " + 
                    (controllers != null ? controllers.size() : 0));
                updateTrackInfoFromControllers(controllers);
            };
            
            mediaSessionManager.addOnActiveSessionsChangedListener(
                sessionsChangedListener, null);
            
            // Get initial active sessions
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);
            updateTrackInfoFromControllers(controllers);
            
        } catch (SecurityException e) {
            android.util.Log.w(TAG, "No permission to access media sessions", e);
        }
    }
    
    /**
     * Update track info from active media controllers
     */
    private void updateTrackInfoFromControllers(List<MediaController> controllers) {
        if (controllers == null || controllers.isEmpty()) {
            android.util.Log.d(TAG, "No active controllers");
            return;
        }
        
        // Find the first controller with valid metadata
        for (MediaController controller : controllers) {
            if (controller == null) continue;
            
            try {
                MediaMetadata metadata = controller.getMetadata();
                PlaybackState playbackState = controller.getPlaybackState();
                
                if (metadata != null) {
                    String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                    String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                    
                    if (title != null && !title.trim().isEmpty()) {
                        // Only update if playback is active
                        if (playbackState != null && 
                            (playbackState.getState() == PlaybackState.STATE_PLAYING ||
                             playbackState.getState() == PlaybackState.STATE_PAUSED)) {
                            
                            MediaSessionTracker.getInstance().updateTrackInfo(title, artist);
                            android.util.Log.d(TAG, "Updated track: " + title + 
                                " by " + (artist != null ? artist : "unknown"));
                            return;
                        }
                    }
                }
            } catch (Exception e) {
                android.util.Log.w(TAG, "Error reading controller metadata", e);
            }
        }
    }
    
    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        // When a new notification is posted, refresh active sessions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);
                updateTrackInfoFromControllers(controllers);
            } catch (SecurityException e) {
                android.util.Log.w(TAG, "No permission to access media sessions", e);
            }
        }
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // When notification is removed, check if we should clear track info
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);
                if (controllers == null || controllers.isEmpty()) {
                    // No active sessions, clear track info
                    MediaSessionTracker.getInstance().clearTrackInfo();
                    android.util.Log.d(TAG, "No active sessions, cleared track info");
                } else {
                    // Refresh from remaining controllers
                    updateTrackInfoFromControllers(controllers);
                }
            } catch (SecurityException e) {
                android.util.Log.w(TAG, "No permission to access media sessions", e);
            }
        }
    }
    
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        android.util.Log.d(TAG, "Listener connected");
        
        // Refresh session info when service connects
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);
                updateTrackInfoFromControllers(controllers);
            } catch (SecurityException e) {
                android.util.Log.w(TAG, "No permission to access media sessions", e);
            }
        }
    }
    
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        android.util.Log.d(TAG, "Listener disconnected");
        
        // Clear track info when service disconnects
        MediaSessionTracker.getInstance().clearTrackInfo();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        android.util.Log.d(TAG, "Service destroyed");
        
        // Remove session listener
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && 
            mediaSessionManager != null && sessionsChangedListener != null) {
            try {
                mediaSessionManager.removeOnActiveSessionsChangedListener(sessionsChangedListener);
            } catch (Exception e) {
                android.util.Log.w(TAG, "Error removing sessions listener", e);
            }
        }
        
        // Clear track info
        MediaSessionTracker.getInstance().clearTrackInfo();
    }
}
