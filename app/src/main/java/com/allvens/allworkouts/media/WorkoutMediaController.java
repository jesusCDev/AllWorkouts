package com.allvens.allworkouts.media;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaController;
import android.media.session.MediaSessionManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.allvens.allworkouts.R;

/**
 * Handles media control functionality for workout sessions
 * Provides simple media controls (previous, play/pause, next) that work with most music apps
 */
public class WorkoutMediaController {
    
    private final Context context;
    private AudioManager audioManager;
    private ImageButton playPauseButton;
    private TextView trackInfoTextView;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable stateUpdateRunnable;
    private long lastClickTime = 0;
    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce
    private static final long STATE_UPDATE_DELAY = 150; // 150ms delay before checking state
    private static final long PERIODIC_UPDATE_INTERVAL = 2000; // 2 seconds
    private Runnable periodicUpdateRunnable;
    private MediaSessionManager mediaSessionManager;
    
    public WorkoutMediaController(Context context) {
        this.context             = context;
        this.audioManager        = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.handler             = new Handler(Looper.getMainLooper());
        this.mediaSessionManager = (MediaSessionManager) context.getSystemService(Context.MEDIA_SESSION_SERVICE);
    }
    
    /**
     * Set up media control buttons with click listeners
     */
    public void setupMediaControls(ImageButton previousButton, ImageButton playPauseButton, 
                                 ImageButton nextButton, TextView trackInfoTextView) {
        this.playPauseButton = playPauseButton;
        this.trackInfoTextView = trackInfoTextView;
        
        // Set up click listeners for media control buttons
        previousButton.setOnClickListener(v -> {
            sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            performHapticFeedback(v);
        });
        
        playPauseButton.setOnClickListener(v -> {
            long currentTime = System.currentTimeMillis();
            
            // Debounce rapid clicks
            if (currentTime - lastClickTime < DEBOUNCE_DELAY) {
                return; // Ignore rapid clicks
            }
            lastClickTime = currentTime;
            
            // Send media button event
            sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE);
            
            // Provide immediate visual feedback by toggling icon
            togglePlayPauseIcon();
            
            // Schedule a delayed state check to sync with actual playback state
            scheduleStateUpdate();
            
            performHapticFeedback(v);
        });
        
        nextButton.setOnClickListener(v -> {
            sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
            performHapticFeedback(v);
        });
        
        // Initialize play/pause button state
        // First set default state to avoid blank button, then check actual state
        isPlaying = audioManager != null && audioManager.isMusicActive();

        updatePlayPauseIcon();
        
        // Use a delay to double-check and refresh state
        handler.postDelayed(() -> {
            refreshPlaybackState();
            updateTrackInfo();
        }, 200);
        
        // Set up periodic refresh of track info
        startPeriodicTrackInfoUpdate();
    }
    
    /**
     * Send media button events to the currently active media session
     */
    private void sendMediaButtonEvent(int keyCode) {
        if (audioManager == null) return;
        
        try {
            // Create the media button event
            KeyEvent downEvent = new KeyEvent(KeyEvent.ACTION_DOWN, keyCode);
            KeyEvent upEvent = new KeyEvent(KeyEvent.ACTION_UP, keyCode);
            
            // Send the media button events
            audioManager.dispatchMediaKeyEvent(downEvent);
            audioManager.dispatchMediaKeyEvent(upEvent);
            
        } catch (Exception e) {
            // Log error but don't crash the app
            android.util.Log.w("WorkoutMediaController", "Failed to send media button event", e);
        }
    }
    
    /**
     * Toggle the play/pause icon and internal state
     */
    private void togglePlayPauseIcon() {
        isPlaying = !isPlaying;
        updatePlayPauseIcon();
    }
    
    /**
     * Update the play/pause button icon based on current state
     */
    private void updatePlayPauseIcon() {
        if (playPauseButton != null) {
            int iconResource = isPlaying ? R.drawable.ic_pause_24dp : R.drawable.ic_play_arrow_24dp;
            playPauseButton.setImageResource(iconResource);
            
            // Debug logging to track state changes
            android.util.Log.d("WorkoutMediaController", "Updating play/pause icon - isPlaying: " + isPlaying + 
                ", setting icon: " + (isPlaying ? "pause" : "play"));
            
            // Update tint for main play/pause button (it's not tinted in XML since it has accent background)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playPauseButton.getDrawable().setTint(context.getResources().getColor(R.color.background_dark));
            }
        }
    }
    
    /**
     * Update the UI to reflect the actual playback state
     */
    public void refreshPlaybackState() {
        if (audioManager != null) {
            boolean actualState = audioManager.isMusicActive();
            android.util.Log.d("WorkoutMediaController", "Refreshing playback state - audioManager.isMusicActive(): " + actualState);
            
            isPlaying = actualState;
            updatePlayPauseIcon();
        }
    }
    
    /**
     * Update track information display with current playing track
     */
    public void updateTrackInfo() {
        if (trackInfoTextView == null) {
            return;
        }
        
        String trackInfo = getCurrentTrackInfo();
        trackInfoTextView.setText(trackInfo);
    }
    
    /**
     * Get current track information from active media sessions
     */
    private String getCurrentTrackInfo() {
        // First, try to get track info from MediaSessionManager if available
        String trackInfo = getTrackInfoFromMediaSessions();
        if (trackInfo != null && !trackInfo.equals("Music Controls")) {
            android.util.Log.d("WorkoutMediaController", "Got track info from MediaSession: " + trackInfo);
            return trackInfo;
        }
        
        // Fallback to showing contextual message based on playback state
        boolean musicActive = audioManager != null && audioManager.isMusicActive();
        android.util.Log.d("WorkoutMediaController", "Music active: " + musicActive);
        
        if (musicActive) {
            return "♪ Playing";
        } else {
            return "♪ Ready to Play";
        }
    }
    
    /**
     * Try to get track information from active media sessions
     * This is a best-effort approach that may not work on all devices/apps
     */
    private String getTrackInfoFromMediaSessions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                // Try to get active sessions without requiring notification listener permission
                // This might work on some devices where the permission check is more lenient
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);
                        
                for (MediaController controller : controllers) {
                    if (controller != null && controller.getMetadata() != null) {
                        MediaMetadata metadata = controller.getMetadata();
                        String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);
                        String artist = metadata.getString(MediaMetadata.METADATA_KEY_ARTIST);
                        
                        if (title != null && !title.trim().isEmpty()) {
                            if (artist != null && !artist.trim().isEmpty()) {
                                return title + " • " + artist;
                            } else {
                                return title;
                            }
                        }
                    }
                }
            } catch (SecurityException e) {
                // Expected - no permission to access media sessions
                android.util.Log.d("WorkoutMediaController", "No permission to access media sessions (this is normal)");
            } catch (Exception e) {
                android.util.Log.w("WorkoutMediaController", "Error getting track info: " + e.getMessage());
            }
        }
        
        return "Music Controls"; // Fallback
    }
    
    /**
     * Start periodic updates of track info to keep it current
     */
    private void startPeriodicTrackInfoUpdate() {
        if (periodicUpdateRunnable != null) {
            handler.removeCallbacks(periodicUpdateRunnable);
        }
        
        periodicUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                // Update both playback state and track info
                refreshPlaybackState();
                updateTrackInfo();
                
                // Schedule next update
                handler.postDelayed(this, PERIODIC_UPDATE_INTERVAL);
            }
        };
        
        // Start the periodic updates
        handler.postDelayed(periodicUpdateRunnable, PERIODIC_UPDATE_INTERVAL);
    }
    
    /**
     * Schedule a delayed state update to sync with actual media player state
     */
    private void scheduleStateUpdate() {
        // Cancel any pending state updates
        if (stateUpdateRunnable != null) {
            handler.removeCallbacks(stateUpdateRunnable);
        }
        
        // Create new state update runnable
        stateUpdateRunnable = () -> {
            if (audioManager != null) {
                boolean actualPlayingState = audioManager.isMusicActive();
                
                // Only update UI if our internal state doesn't match the actual state
                if (isPlaying != actualPlayingState) {
                    isPlaying = actualPlayingState;
                    updatePlayPauseIcon();
                }
                
                // Also refresh track info in case it changed
                updateTrackInfo();
            }
        };
        
        // Schedule the state update after a delay to allow media apps to respond
        handler.postDelayed(stateUpdateRunnable, STATE_UPDATE_DELAY);
    }
    
    /**
     * Add haptic feedback for button presses if available
     */
    private void performHapticFeedback(android.view.View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            view.performHapticFeedback(android.view.HapticFeedbackConstants.VIRTUAL_KEY);
        }
    }
    
    /**
     * Clean up resources
     */
    public void cleanup() {
        // Cancel any pending state updates
        if (handler != null) {
            if (stateUpdateRunnable != null) {
                handler.removeCallbacks(stateUpdateRunnable);
            }
            if (periodicUpdateRunnable != null) {
                handler.removeCallbacks(periodicUpdateRunnable);
            }
        }
        
        playPauseButton = null;
        trackInfoTextView = null;
        audioManager = null;
        handler = null;
        stateUpdateRunnable = null;
        periodicUpdateRunnable = null;
        mediaSessionManager = null;
    }
}