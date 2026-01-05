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
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.PreferencesValues;
import com.allvens.allworkouts.settings_manager.SettingsPrefsManager;

/**
 * Handles media control functionality for workout sessions
 * Provides simple media controls (previous, play/pause, next) that work with most music apps
 */
public class WorkoutMediaController {
    
    private final Context context;
    private AudioManager audioManager;
    private ImageButton playPauseButton;
    private ImageButton previousButton;
    private ImageButton nextButton;
    private TextView trackTitleTextView;
    private boolean isPlaying = false;
    private Handler handler;
    private Runnable stateUpdateRunnable;
    private long lastClickTime = 0;
    private static final long DEBOUNCE_DELAY = 300; // 300ms debounce
    private static final long STATE_UPDATE_DELAY = 150; // 150ms delay before checking state
    private static final long PERIODIC_UPDATE_INTERVAL = 1000; // 1 second (faster updates)
    private static final long FEEDBACK_DURATION = 150; // 150ms visual feedback
    private Runnable periodicUpdateRunnable;
    private MediaSessionManager mediaSessionManager;
    private boolean showSongTitle = false;
    
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
                                 ImageButton nextButton, TextView trackTitleTextView) {
        this.previousButton = previousButton;
        this.playPauseButton = playPauseButton;
        this.nextButton = nextButton;
        this.trackTitleTextView = trackTitleTextView;

        // Load show song title preference
        SettingsPrefsManager prefsManager = new SettingsPrefsManager(context);
        showSongTitle = prefsManager.getPrefSetting(PreferencesValues.SHOW_SONG_TITLE, false);

        // Set up click listeners for media control buttons
        previousButton.setOnClickListener(v -> {
            sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_PREVIOUS);
            performHapticFeedback(v);
            showButtonFeedback(previousButton, null);
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
            
            // Show visual feedback
            showButtonFeedback(playPauseButton, null);
            
            performHapticFeedback(v);
            
            // Sync state after a longer delay (let media app respond)
            handler.postDelayed(this::refreshPlaybackState, 500);
        });
        
        nextButton.setOnClickListener(v -> {
            sendMediaButtonEvent(KeyEvent.KEYCODE_MEDIA_NEXT);
            performHapticFeedback(v);
            showButtonFeedback(nextButton, null);
        });
        
        // Initialize play/pause button state
        // First set default state to avoid blank button, then check actual state
        isPlaying = audioManager != null && audioManager.isMusicActive();

        updatePlayPauseIcon();

        // Update track title visibility based on setting
        updateTrackTitleVisibility();
        
        // Use a delay to double-check and refresh state
        handler.postDelayed(() -> {
            refreshPlaybackState();
            updateTrackInfo();
        }, 200);
        
        // Set up periodic refresh of track info
        startPeriodicTrackInfoUpdate();
    }
    
    /**
     * Show visual feedback when a button is pressed
     */
    private void showButtonFeedback(ImageButton button, String feedbackText) {
        // Animate the specific button with a glow/pulse effect
        if (button != null) {
            pulseButtonWithGlow(button);
        }
    }
    
    /**
     * Pulse animation with glow effect for a button
     */
    private void pulseButtonWithGlow(ImageButton button) {
        // Create a subtle glow by animating alpha and scale
        // First, brighten the button
        button.setAlpha(1.0f);
        
        // Scale up smoothly
        button.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .alpha(0.7f)
            .setDuration(150)
            .setInterpolator(new android.view.animation.DecelerateInterpolator())
            .withEndAction(() -> {
                // Scale back down with a gentle bounce
                button.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .alpha(1.0f)
                    .setDuration(250)
                    .setInterpolator(new android.view.animation.OvershootInterpolator(1.5f))
                    .start();
            })
            .start();
    }
    
    /**
     * Update track title visibility based on setting
     */
    private void updateTrackTitleVisibility() {
        if (trackTitleTextView != null) {
            trackTitleTextView.setVisibility(showSongTitle ? View.VISIBLE : View.GONE);
        }
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
        if (trackTitleTextView == null) {
            return;
        }

        MediaSessionTracker tracker = MediaSessionTracker.getInstance();
        String title = tracker.getCurrentTitle();

        // Try MediaSessionManager fallback if no title from tracker
        if (title == null || title.isEmpty()) {
            title = getTrackTitleFromMediaSessions();
        }

        // Update title
        if (title != null && !title.isEmpty()) {
            trackTitleTextView.setText(title);
            // Enable marquee scrolling for long titles
            trackTitleTextView.setSelected(true);
        } else {
            // Fallback based on playback state
            boolean musicActive = audioManager != null && audioManager.isMusicActive();
            trackTitleTextView.setText(musicActive ? "Playing" : "Ready to Play");
        }
    }

    /**
     * Get track title from MediaSessions
     * @return Track title or null
     */
    private String getTrackTitleFromMediaSessions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mediaSessionManager != null) {
            try {
                List<MediaController> controllers = mediaSessionManager.getActiveSessions(null);

                for (MediaController controller : controllers) {
                    if (controller != null && controller.getMetadata() != null) {
                        MediaMetadata metadata = controller.getMetadata();
                        String title = metadata.getString(MediaMetadata.METADATA_KEY_TITLE);

                        if (title != null && !title.trim().isEmpty()) {
                            return title;
                        }
                    }
                }
            } catch (SecurityException e) {
                // Expected - no permission
            } catch (Exception e) {
                android.util.Log.w("WorkoutMediaController", "Error getting track info: " + e.getMessage());
            }
        }
        return null;
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
        trackTitleTextView = null;
        audioManager = null;
        handler = null;
        stateUpdateRunnable = null;
        periodicUpdateRunnable = null;
        mediaSessionManager = null;
    }
}