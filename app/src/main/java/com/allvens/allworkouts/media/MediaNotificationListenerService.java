package com.allvens.allworkouts.media;

import android.content.ComponentName;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.media.session.PlaybackState;
import android.os.Build;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.annotation.RequiresApi;

import java.util.ArrayList;
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
    private List<MediaController> activeControllers = new ArrayList<>();
    private MediaController.Callback mediaCallback;
    
    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.d(TAG, "Service created");
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mediaSessionManager = (MediaSessionManager) getSystemService(MEDIA_SESSION_SERVICE);
            createMediaCallback();
        }
    }
    
    /**
     * Create the media callback that listens for metadata changes
     */
    private void createMediaCallback() {
        mediaCallback = new MediaController.Callback() {
            @Override
            public void onMetadataChanged(MediaMetadata metadata) {
                android.util.Log.d(TAG, "Metadata changed callback");
                if (metadata != null) {
                    String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                    String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                    
                    if (title != null && !title.trim().isEmpty()) {
                        MediaSessionTracker.getInstance().updateTrackInfo(title, artist);
                        android.util.Log.d(TAG, "Callback updated track: " + title);
                    }
                }
            }
            
            @Override
            public void onPlaybackStateChanged(PlaybackState state) {
                android.util.Log.d(TAG, "Playback state changed: " + 
                    (state != null ? state.getState() : "null"));
                // Refresh metadata when playback changes
                refreshActiveControllers();
            }
        };
    }
    
    /**
     * Set up listener for active media session changes
     */
    private void setupSessionListener() {
        if (mediaSessionManager == null) return;
        
        try {
            ComponentName componentName = new ComponentName(this, MediaNotificationListenerService.class);
            
            sessionsChangedListener = controllers -> {
                android.util.Log.d(TAG, "Active sessions changed, count: " + 
                    (controllers != null ? controllers.size() : 0));
                registerControllersCallbacks(controllers);
                updateTrackInfoFromControllers(controllers);
            };
            
            mediaSessionManager.addOnActiveSessionsChangedListener(
                sessionsChangedListener, componentName);
            
            // Get initial active sessions
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
            registerControllersCallbacks(controllers);
            updateTrackInfoFromControllers(controllers);
            
        } catch (SecurityException e) {
            android.util.Log.w(TAG, "No permission to access media sessions", e);
        }
    }
    
    /**
     * Register callbacks on all active media controllers
     */
    private void registerControllersCallbacks(List<MediaController> controllers) {
        // Unregister from old controllers
        for (MediaController oldController : activeControllers) {
            try {
                oldController.unregisterCallback(mediaCallback);
            } catch (Exception e) {
                // Ignore
            }
        }
        activeControllers.clear();
        
        // Register on new controllers
        if (controllers != null) {
            for (MediaController controller : controllers) {
                if (controller != null) {
                    try {
                        controller.registerCallback(mediaCallback);
                        activeControllers.add(controller);
                        android.util.Log.d(TAG, "Registered callback on: " + controller.getPackageName());
                    } catch (Exception e) {
                        android.util.Log.w(TAG, "Failed to register callback", e);
                    }
                }
            }
        }
    }
    
    /**
     * Refresh track info from active controllers
     */
    private void refreshActiveControllers() {
        if (mediaSessionManager == null) return;
        
        try {
            ComponentName componentName = new ComponentName(this, MediaNotificationListenerService.class);
            List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
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
        // Try to extract track info directly from media notifications
        if (sbn != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            extractTrackFromNotification(sbn);
        }
        
        // Also refresh active sessions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            refreshActiveControllers();
        }
    }
    
    /**
     * Try to extract track info directly from notification content
     * This works for most music apps that show track info in notifications
     */
    private void extractTrackFromNotification(StatusBarNotification sbn) {
        try {
            String packageName = sbn.getPackageName();
            
            // Check if this looks like a media notification
            android.app.Notification notification = sbn.getNotification();
            if (notification == null) return;
            
            // Check notification category for media
            String category = notification.category;
            boolean isMediaNotification = android.app.Notification.CATEGORY_TRANSPORT.equals(category) ||
                                         android.app.Notification.CATEGORY_SERVICE.equals(category);
            
            // Also check common music app packages
            boolean isMusicApp = packageName.contains("music") ||
                                packageName.contains("spotify") ||
                                packageName.contains("youtube") ||
                                packageName.contains("pandora") ||
                                packageName.contains("soundcloud") ||
                                packageName.contains("deezer") ||
                                packageName.contains("tidal") ||
                                packageName.contains("amazon") ||
                                packageName.contains("apple") ||
                                packageName.contains("google.android.apps.youtube") ||
                                packageName.contains("maxmpz") ||  // Poweramp
                                packageName.contains("audioplayer");  // Poweramp alternate
            
            if (!isMediaNotification && !isMusicApp) {
                return;
            }
            
            android.util.Log.d(TAG, "Processing media notification from: " + packageName);
            
            // Extract from notification extras
            android.os.Bundle extras = notification.extras;
            if (extras != null) {
                // Try to get media session from notification
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    android.media.session.MediaSession.Token token = 
                        extras.getParcelable(android.app.Notification.EXTRA_MEDIA_SESSION);
                    if (token != null) {
                        MediaController controller = new MediaController(this, token);
                        MediaMetadata metadata = controller.getMetadata();
                        if (metadata != null) {
                            String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                            String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                            if (title != null && !title.trim().isEmpty()) {
                                MediaSessionTracker.getInstance().updateTrackInfo(title, artist);
                                android.util.Log.d(TAG, "Got track from notification token: " + title);
                                return;
                            }
                        }
                    }
                }
                
                // Fallback: try to get title/text from notification content
                CharSequence title = extras.getCharSequence(android.app.Notification.EXTRA_TITLE);
                CharSequence text = extras.getCharSequence(android.app.Notification.EXTRA_TEXT);
                CharSequence bigText = extras.getCharSequence(android.app.Notification.EXTRA_BIG_TEXT);
                
                String trackTitle = null;
                String trackArtist = null;
                
                if (title != null && title.length() > 0) {
                    trackTitle = title.toString();
                }
                
                if (text != null && text.length() > 0) {
                    trackArtist = text.toString();
                }
                
                // Update if we got track info
                if (trackTitle != null && !trackTitle.trim().isEmpty()) {
                    MediaSessionTracker.getInstance().updateTrackInfo(trackTitle, trackArtist);
                    android.util.Log.d(TAG, "Got track from notification extras: " + trackTitle + 
                        (trackArtist != null ? " - " + trackArtist : ""));
                }
            }
        } catch (Exception e) {
            android.util.Log.w(TAG, "Error extracting track from notification", e);
        }
    }
    
    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // When notification is removed, check if we should clear track info
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                ComponentName componentName = new ComponentName(this, MediaNotificationListenerService.class);
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(componentName);
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
        android.util.Log.d(TAG, "Listener connected - setting up session listener");
        
        // Set up session listener when service connects (permission is now granted)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            setupSessionListener();
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
        
        // Unregister from all controllers
        for (MediaController controller : activeControllers) {
            try {
                controller.unregisterCallback(mediaCallback);
            } catch (Exception e) {
                // Ignore
            }
        }
        activeControllers.clear();
        
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
