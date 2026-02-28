package com.allvens.allworkouts.widget;

import android.content.Context;

import com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker;
import com.allvens.allworkouts.data_manager.database.WorkoutInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;
import com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus;

import java.util.Calendar;
import java.util.List;
import java.util.Set;

public class WidgetDataHelper {

    public static class WidgetData {
        /** Workout count per day for the 9-day window (index 0–8, today = 4). */
        public int[] dayCounts = new int[9];
        /** Current streak (consecutive days with workouts, counting back from today). */
        public int streak;
        /** Whether the next workout is a max-out session (progress >= 8). */
        public boolean nextIsMaxOut;
        /** Which cells are predicted max days (index 0–8, today = 4). */
        public boolean[] isMaxDay = new boolean[9];
    }

    public static WidgetData gatherWidgetData(Context context) {
        WidgetData data = new WidgetData();
        WorkoutWrapper wrapper = new WorkoutWrapper(context);

        try {
            wrapper.open();

            // Today at midnight
            Calendar today = Calendar.getInstance();
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            // 9-day window: -4 to +4 relative to today
            for (int offset = -4; offset <= 4; offset++) {
                Calendar day = (Calendar) today.clone();
                day.add(Calendar.DAY_OF_MONTH, offset);
                long dayStart = day.getTimeInMillis() / 1000;
                long dayEnd = dayStart + (24 * 60 * 60) - 1;
                data.dayCounts[offset + 4] = wrapper.getWorkoutCountForDay(dayStart, dayEnd);
            }

            // Current streak: count back from today
            data.streak = 0;
            Calendar check = (Calendar) today.clone();
            for (int i = 0; i < 365; i++) {
                long dayStart = check.getTimeInMillis() / 1000;
                long dayEnd = dayStart + (24 * 60 * 60) - 1;
                if (wrapper.getWorkoutCountForDay(dayStart, dayEnd) > 0) {
                    data.streak++;
                    check.add(Calendar.DAY_OF_MONTH, -1);
                } else {
                    break;
                }
            }

            // Next workout max-out check
            WorkoutPosAndStatus[] enabled = new WorkoutBasicsPrefsChecker(context)
                    .getWorkoutPositions(false);

            long todayStart = today.getTimeInMillis() / 1000;
            long todayEnd = todayStart + (24 * 60 * 60) - 1;
            Set<Long> completedIds = wrapper.getUniqueWorkoutIdsForDay(todayStart, todayEnd);

            List<WorkoutInfo> allWorkouts = wrapper.getAllWorkouts();
            java.util.Set<String> completedNames = new java.util.HashSet<>();
            for (WorkoutInfo w : allWorkouts) {
                if (completedIds.contains(w.getId())) {
                    completedNames.add(w.getWorkout().toLowerCase());
                }
            }

            data.nextIsMaxOut = false;
            for (WorkoutPosAndStatus w : enabled) {
                if (!completedNames.contains(w.getName().toLowerCase())) {
                    WorkoutInfo info = wrapper.getWorkout(w.getName());
                    if (info != null) {
                        data.nextIsMaxOut = info.getProgress() >= 8;
                    }
                    break;
                }
            }

            // Predict max days for cells (index 4 = today, 5-8 = tomorrow to +4 days)
            for (WorkoutPosAndStatus w : enabled) {
                WorkoutInfo info = wrapper.getWorkout(w.getName());
                if (info == null) continue;

                boolean doneToday = completedNames.contains(w.getName().toLowerCase());

                int sessionsUntilMax;
                if (info.getProgress() >= 8 && !doneToday) {
                    // Already at max day — today IS the max day
                    sessionsUntilMax = 0;
                } else {
                    int projectedProgress = info.getProgress() + (doneToday ? 0 : 1);
                    sessionsUntilMax = 8 - projectedProgress;
                }

                // sessionsUntilMax maps directly to cell offset from today (index 4)
                if (sessionsUntilMax >= 0 && sessionsUntilMax <= 4) {
                    data.isMaxDay[4 + sessionsUntilMax] = true;
                }
            }

        } catch (Exception e) {
            android.util.Log.e("WidgetDataHelper", "Error gathering widget data", e);
        } finally {
            wrapper.close();
        }

        return data;
    }
}
