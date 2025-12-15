package com.allvens.allworkouts.media;

/**
 * Singleton class to store and provide current media track information
 * Updated by MediaNotificationListenerService when media notifications change
 */
public class MediaSessionTracker {
    
    private static MediaSessionTracker instance;
    
    private String currentTitle;
    private String currentArtist;
    private long lastUpdateTime;
    
    private MediaSessionTracker() {
        this.currentTitle = null;
        this.currentArtist = null;
        this.lastUpdateTime = 0;
    }
    
    /**
     * Get the singleton instance
     */
    public static synchronized MediaSessionTracker getInstance() {
        if (instance == null) {
            instance = new MediaSessionTracker();
        }
        return instance;
    }
    
    /**
     * Update current track information
     */
    public synchronized void updateTrackInfo(String title, String artist) {
        this.currentTitle = title;
        this.currentArtist = artist;
        this.lastUpdateTime = System.currentTimeMillis();
        
        android.util.Log.d("MediaSessionTracker", "Track updated: " + title + 
            (artist != null ? " • " + artist : ""));
    }
    
    /**
     * Clear current track information
     */
    public synchronized void clearTrackInfo() {
        this.currentTitle = null;
        this.currentArtist = null;
        this.lastUpdateTime = 0;
        
        android.util.Log.d("MediaSessionTracker", "Track info cleared");
    }
    
    /**
     * Get formatted track info string
     * Returns null if no track info available
     */
    public synchronized String getFormattedTrackInfo() {
        if (currentTitle == null || currentTitle.trim().isEmpty()) {
            return null;
        }
        
        if (currentArtist != null && !currentArtist.trim().isEmpty()) {
            return currentTitle + " • " + currentArtist;
        }
        
        return currentTitle;
    }
    
    /**
     * Check if track info is available and recent (within last 10 seconds)
     */
    public synchronized boolean hasRecentTrackInfo() {
        if (currentTitle == null) {
            return false;
        }
        
        long timeSinceUpdate = System.currentTimeMillis() - lastUpdateTime;
        return timeSinceUpdate < 10000; // 10 seconds
    }
    
    /**
     * Get current title
     */
    public synchronized String getCurrentTitle() {
        return currentTitle;
    }
    
    /**
     * Get current artist
     */
    public synchronized String getCurrentArtist() {
        return currentArtist;
    }
}
