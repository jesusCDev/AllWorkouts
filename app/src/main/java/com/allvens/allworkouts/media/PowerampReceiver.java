package com.allvens.allworkouts.media;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Broadcast receiver for Poweramp track changes
 * Receives intents from Poweramp when track changes or playback state changes
 *
 * Poweramp API documentation: https://github.com/maxmpz/powerampapi
 */
public class PowerampReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerampReceiver";

    // Poweramp intent actions
    public static final String ACTION_TRACK_CHANGED = "com.maxmpz.audioplayer.TRACK_CHANGED";
    public static final String ACTION_STATUS_CHANGED = "com.maxmpz.audioplayer.STATUS_CHANGED";
    public static final String ACTION_PLAYING_MODE_CHANGED = "com.maxmpz.audioplayer.PLAYING_MODE_CHANGED";

    // Track info extras (available since Poweramp build 948 directly in STATUS_CHANGED)
    public static final String EXTRA_TITLE = "title";
    public static final String EXTRA_ARTIST = "artist";
    public static final String EXTRA_ALBUM = "album";
    public static final String EXTRA_TRACK = "track";  // Legacy bundle containing track info

    // Status extras
    public static final String EXTRA_STATE = "state";

    // Playback states
    public static final int STATE_STOPPED = 0;
    public static final int STATE_PLAYING = 1;
    public static final int STATE_PAUSED = 2;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) {
            return;
        }

        String action = intent.getAction();
        android.util.Log.d(TAG, "Received action: " + action);

        Bundle extras = intent.getExtras();
        if (extras == null) {
            android.util.Log.d(TAG, "No extras in intent");
            return;
        }

        // Log all extras for debugging
        for (String key : extras.keySet()) {
            Object value = extras.get(key);
            android.util.Log.d(TAG, "  Extra: " + key + " = " + value);
        }

        switch (action) {
            case ACTION_TRACK_CHANGED:
                handleTrackChanged(extras);
                break;

            case ACTION_STATUS_CHANGED:
                handleStatusChanged(extras);
                break;

            case ACTION_PLAYING_MODE_CHANGED:
                // Mode changes (shuffle, repeat, etc.) - not needed for track info
                break;
        }
    }

    /**
     * Handle track changed intent
     */
    private void handleTrackChanged(Bundle extras) {
        String title = extractTitle(extras);
        String artist = extractArtist(extras);

        if (title != null && !title.trim().isEmpty()) {
            MediaSessionTracker.getInstance().updateTrackInfo(title, artist);
            android.util.Log.d(TAG, "Track changed: " + title + " by " + (artist != null ? artist : "unknown"));
        }
    }

    /**
     * Handle status changed intent (includes track info since build 948)
     */
    private void handleStatusChanged(Bundle extras) {
        int state = extras.getInt(EXTRA_STATE, -1);
        android.util.Log.d(TAG, "Status changed, state: " + state);

        // Try to extract track info from status intent (Poweramp build 948+)
        String title = extractTitle(extras);
        String artist = extractArtist(extras);

        if (title != null && !title.trim().isEmpty()) {
            if (state == STATE_PLAYING || state == STATE_PAUSED) {
                MediaSessionTracker.getInstance().updateTrackInfo(title, artist);
                android.util.Log.d(TAG, "Status update - Track: " + title);
            }
        }

        // Clear track info if stopped
        if (state == STATE_STOPPED) {
            MediaSessionTracker.getInstance().clearTrackInfo();
            android.util.Log.d(TAG, "Playback stopped, cleared track info");
        }
    }

    /**
     * Extract title from intent extras
     * Handles both new (direct) and legacy (bundle) formats
     */
    private String extractTitle(Bundle extras) {
        // Try direct extra first (Poweramp build 948+)
        String title = extras.getString(EXTRA_TITLE);
        if (title != null && !title.trim().isEmpty()) {
            return title;
        }

        // Fallback to legacy track bundle
        Bundle trackBundle = extras.getBundle(EXTRA_TRACK);
        if (trackBundle != null) {
            title = trackBundle.getString(EXTRA_TITLE);
            if (title != null && !title.trim().isEmpty()) {
                return title;
            }
        }

        return null;
    }

    /**
     * Extract artist from intent extras
     * Handles both new (direct) and legacy (bundle) formats
     */
    private String extractArtist(Bundle extras) {
        // Try direct extra first (Poweramp build 948+)
        String artist = extras.getString(EXTRA_ARTIST);
        if (artist != null && !artist.trim().isEmpty()) {
            return artist;
        }

        // Fallback to legacy track bundle
        Bundle trackBundle = extras.getBundle(EXTRA_TRACK);
        if (trackBundle != null) {
            artist = trackBundle.getString(EXTRA_ARTIST);
            if (artist != null && !artist.trim().isEmpty()) {
                return artist;
            }
        }

        return null;
    }
}
