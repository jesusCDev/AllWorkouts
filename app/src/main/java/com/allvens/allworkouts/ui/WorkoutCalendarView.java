package com.allvens.allworkouts.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.allvens.allworkouts.R;
import com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo;
import com.allvens.allworkouts.data_manager.database.WorkoutWrapper;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Custom calendar view that shows workout activity for the current month
 * Uses color coding to indicate workout intensity per day
 */
public class WorkoutCalendarView extends View {
    
    private static final String TAG = "WorkoutCalendarView";
    
    // Modern heat-map intensity colors
    private int[] heatColors = new int[5]; // [empty, low, medium, high, max]
    private int colorAccent;
    private int colorTextPrimary;
    private int colorTextSecondary;
    private int colorTodayStroke;
    private int colorSelectedStroke;
    private int colorRestDay;
    private int colorMaxDayStroke;
    
    // Calendar data
    private Calendar calendar;
    private Map<Integer, Integer> workoutCounts; // day -> workout count
    private Map<Integer, Boolean> maxWorkoutDays; // day -> is max workout day
    private Map<Integer, java.util.List<String>> maxWorkoutNames; // day -> list of workout names with max
    private int daysInMonth;
    private int firstDayOfWeek;
    private int totalWorkoutTypes = 4; // Pull-ups, Push-ups, Sit-ups, Squats
    private int currentStreak = 0;
    private int maxStreak = 0;
    private int todayDay = -1;
    private int selectedDay = -1; // For interactive selection
    
    // Drawing objects
    private Paint textPaint;
    private Paint cellPaint;
    private Paint headerPaint;
    private Paint monthHeaderPaint;
    private Paint strokePaint;
    
    // Modern grid dimensions
    private float cellSize; // Heat cells are square
    private float cellGap;
    private float cellRadius;
    private int headerHeight;
    private int monthHeaderHeight;
    private Rect textBounds = new Rect();
    
    // Grid positioning
    private float gridStartX;
    private float gridStartY;
    
    // Calendar grid
    private static final String[] DAYS = {"S", "M", "T", "W", "T", "F", "S"};
    private int weeksToShow = 5; // Will be calculated dynamically

    // Gesture detection for swipe navigation
    private GestureDetector gestureDetector;
    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    
    public WorkoutCalendarView(Context context) {
        super(context);
        init(context, null, 0);
    }
    
    public WorkoutCalendarView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }
    
    public WorkoutCalendarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }
    
    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        // Load modern heat-map colors from design tokens
        heatColors[0] = context.getResources().getColor(R.color.heat_empty);   // No workout
        heatColors[1] = context.getResources().getColor(R.color.heat_low);     // 15% intensity
        heatColors[2] = context.getResources().getColor(R.color.heat_medium);  // 30% intensity
        heatColors[3] = context.getResources().getColor(R.color.heat_high);    // 60% intensity
        heatColors[4] = context.getResources().getColor(R.color.heat_max);     // 100% intensity
        
        colorAccent = context.getResources().getColor(R.color.accent_primary);
        colorTextPrimary = context.getResources().getColor(R.color.text_primary);
        colorTextSecondary = context.getResources().getColor(R.color.text_secondary);
        colorTodayStroke = context.getResources().getColor(R.color.text_primary);
        colorSelectedStroke = context.getResources().getColor(R.color.accent_primary);
        colorRestDay = context.getResources().getColor(R.color.rest_day);
        colorMaxDayStroke = context.getResources().getColor(R.color.max_day_yellow); // Yellow for max days
        
        // Load modern dimensions
        cellSize = context.getResources().getDimensionPixelSize(R.dimen.heat_cell_size);
        cellGap = context.getResources().getDimensionPixelSize(R.dimen.heat_cell_gap);
        cellRadius = context.getResources().getDimensionPixelSize(R.dimen.heat_cell_radius);
        
        // Initialize calendar
        calendar = Calendar.getInstance();
        workoutCounts = new HashMap<>();
        maxWorkoutDays = new HashMap<>();
        maxWorkoutNames = new HashMap<>();
        updateCalendarData();
        
        // Initialize modern paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.SUBPIXEL_TEXT_FLAG);
        textPaint.setColor(colorTextPrimary);
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_caption)); // 14sp for better readability
        textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setHinting(Paint.HINTING_ON);
        textPaint.setFilterBitmap(true);
        
        cellPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        headerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        headerPaint.setColor(colorTextSecondary);
        headerPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_caption));
        headerPaint.setTypeface(Typeface.DEFAULT_BOLD);
        headerPaint.setTextAlign(Paint.Align.CENTER);
        
        monthHeaderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        monthHeaderPaint.setColor(colorTextPrimary);
        monthHeaderPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_title));
        monthHeaderPaint.setTypeface(Typeface.DEFAULT_BOLD);
        monthHeaderPaint.setTextAlign(Paint.Align.CENTER);
        
        strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        strokePaint.setStyle(Paint.Style.STROKE);
        strokePaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.stroke_standard));

        // Initialize gesture detector for swipe navigation
        gestureDetector = new GestureDetector(context, new SwipeGestureListener());

        // Load workout data
        loadWorkoutData();
    }

    /**
     * Handle touch events for swipe gesture detection and cell clicks
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Let gesture detector handle the event first
        boolean handled = gestureDetector.onTouchEvent(event);

        // Handle tap events for max day cells
        if (event.getAction() == MotionEvent.ACTION_UP) {
            handleCellClick(event.getX(), event.getY());
        }

        // Must return true for ACTION_DOWN to receive subsequent events
        return handled || event.getAction() == MotionEvent.ACTION_DOWN || super.onTouchEvent(event);
    }
    
    /**
     * Handle click on a calendar cell
     */
    private void handleCellClick(float x, float y) {
        // Check if click is within the grid
        if (gridStartX <= 0 || gridStartY <= 0) return;
        
        // Calculate which cell was clicked
        float relativeX = x - gridStartX;
        float relativeY = y - gridStartY;
        
        if (relativeX < 0 || relativeY < 0) return;
        
        int dayOfWeek = (int) (relativeX / (cellSize + cellGap));
        int week = (int) (relativeY / (cellSize + cellGap));
        
        if (dayOfWeek < 0 || dayOfWeek >= 7 || week < 0 || week >= weeksToShow) return;
        
        // Calculate which day this corresponds to
        int day = calculateDayFromCell(week, dayOfWeek);
        
        if (day > 0 && day <= daysInMonth) {
            // Check if this day has max workouts
            java.util.List<String> workouts = maxWorkoutNames.get(day);
            if (workouts != null && !workouts.isEmpty()) {
                showMaxWorkoutDialog(day, workouts);
            }
        }
    }
    
    /**
     * Calculate which day of month a cell represents
     */
    private int calculateDayFromCell(int week, int dayOfWeek) {
        int currentDay = 1;
        
        for (int w = 0; w < weeksToShow; w++) {
            for (int d = 0; d < 7; d++) {
                if (w == 0 && d < firstDayOfWeek) {
                    continue; // Empty cell before first day
                }
                
                if (currentDay > daysInMonth) {
                    return -1; // Past end of month
                }
                
                if (w == week && d == dayOfWeek) {
                    return currentDay;
                }
                
                currentDay++;
            }
        }
        
        return -1;
    }
    
    /**
     * Show dialog with max workout information for a specific day
     */
    private void showMaxWorkoutDialog(int day, java.util.List<String> workouts) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext(), R.style.DarkAlertDialog);
        
        String title = "Max Workout" + (workouts.size() > 1 ? "s" : "") + " on Day " + day;
        StringBuilder message = new StringBuilder();
        
        for (int i = 0; i < workouts.size(); i++) {
            message.append(workouts.get(i));
            if (i < workouts.size() - 1) {
                message.append("\n");
            }
        }
        
        builder.setTitle(title)
               .setMessage(message.toString())
               .setPositiveButton("OK", null)
               .show();
    }

    /**
     * Gesture listener for detecting horizontal swipes
     */
    private class SwipeGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true; // Required to receive other gesture events
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (e1 == null || e2 == null) return false;

            float diffX = e2.getX() - e1.getX();
            float diffY = e2.getY() - e1.getY();

            // Check if horizontal swipe (more horizontal than vertical)
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe right -> previous month
                        previousMonth();
                    } else {
                        // Swipe left -> next month
                        nextMonth();
                    }
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Navigate to the previous month
     */
    public void previousMonth() {
        calendar.add(Calendar.MONTH, -1);
        updateCalendarData();
        loadWorkoutData();
        requestLayout();
        invalidate();
        Log.d(TAG, "Navigated to previous month: " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
    }

    /**
     * Navigate to the next month
     */
    public void nextMonth() {
        calendar.add(Calendar.MONTH, 1);
        updateCalendarData();
        loadWorkoutData();
        requestLayout();
        invalidate();
        Log.d(TAG, "Navigated to next month: " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
    }

    /**
     * Navigate to the current month (today)
     */
    public void goToCurrentMonth() {
        calendar = Calendar.getInstance();
        updateCalendarData();
        loadWorkoutData();
        requestLayout();
        invalidate();
    }
    
    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            // Refresh calendar when view becomes visible
            post(() -> {
                updateCalendarData();
                requestLayout();
                invalidate();
            });
        }
    }
    
    private void updateCalendarData() {
        // Get today's day of month
        Calendar today = Calendar.getInstance();
        if (today.get(Calendar.MONTH) == calendar.get(Calendar.MONTH) && 
            today.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            todayDay = today.get(Calendar.DAY_OF_MONTH);
        } else {
            todayDay = -1; // Not viewing current month
        }
        
        // Get first day of current month
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // Make Sunday = 0
        
        // Calculate how many weeks we actually need for this month
        int totalCells = firstDayOfWeek + daysInMonth;
        weeksToShow = (int) Math.ceil(totalCells / 7.0);
        // Ensure we have enough weeks - no artificial minimum that could cut off dates
        // Some months need 6 weeks (e.g., 31-day months starting on Friday/Saturday)
        
        Log.d(TAG, "Month: " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        Log.d(TAG, "Days in month: " + daysInMonth + ", First day: " + firstDayOfWeek + ", Total cells: " + totalCells + ", Calculated weeks: " + weeksToShow + ", Today: " + todayDay);
        
        // Debug calendar layout to ensure all dates fit
        debugCalendarLayout();
    }
    
    private void loadWorkoutData() {
        workoutCounts.clear();

        WorkoutWrapper workoutWrapper = new WorkoutWrapper(getContext());
        try {
            workoutWrapper.open();

            // Get actual number of workout types from database
            List<com.allvens.allworkouts.data_manager.database.WorkoutInfo> allWorkouts =
                workoutWrapper.getAllWorkouts();
            totalWorkoutTypes = Math.max(allWorkouts.size(), 1); // At least 1 to avoid division by zero

            // For each day in the current month, count UNIQUE workout types completed
            Calendar dayCalendar = (Calendar) calendar.clone();

            for (int day = 1; day <= daysInMonth; day++) {
                dayCalendar.set(Calendar.DAY_OF_MONTH, day);
                int uniqueWorkoutCount = getUniqueWorkoutCountForDay(workoutWrapper, dayCalendar);
                if (uniqueWorkoutCount > 0) {
                    workoutCounts.put(day, uniqueWorkoutCount);
                    Log.d(TAG, "Day " + day + ": " + uniqueWorkoutCount + "/" + totalWorkoutTypes + " workout types");
                }
            }

            // Calculate streaks
            calculateStreaks();
            
            // Calculate upcoming max workout days
            calculateMaxWorkoutDays(workoutWrapper, allWorkouts);

        } catch (Exception e) {
            Log.e(TAG, "Error loading workout data", e);
        } finally {
            workoutWrapper.close();
        }
    }
    
    /**
     * Calculate which future days will have max workouts.
     * Assumes today's workout will be completed (if not already done)
     * and that one workout is completed each subsequent day.
     */
    private void calculateMaxWorkoutDays(WorkoutWrapper wrapper, List<com.allvens.allworkouts.data_manager.database.WorkoutInfo> allWorkouts) {
        maxWorkoutDays.clear();
        maxWorkoutNames.clear();

        // Only calculate if we're viewing the current month
        if (todayDay <= 0) {
            return;
        }

        try {
            // Only consider enabled workouts
            com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus[] enabledPositions =
                    new com.allvens.allworkouts.data_manager.WorkoutBasicsPrefsChecker(getContext())
                            .getWorkoutPositions(false);
            java.util.Set<String> enabledNames = new java.util.HashSet<>();
            for (com.allvens.allworkouts.settings_manager.WorkoutPos.WorkoutPosAndStatus pos : enabledPositions) {
                enabledNames.add(pos.getName().toLowerCase());
            }

            // Check which workout types were already completed today
            java.util.Calendar today = java.util.Calendar.getInstance();
            today.set(java.util.Calendar.HOUR_OF_DAY, 0);
            today.set(java.util.Calendar.MINUTE, 0);
            today.set(java.util.Calendar.SECOND, 0);
            today.set(java.util.Calendar.MILLISECOND, 0);
            long todayStart = today.getTimeInMillis() / 1000;
            long todayEnd = todayStart + 86400 - 1;
            java.util.Set<Long> completedTodayIds = wrapper.getUniqueWorkoutIdsForDay(todayStart, todayEnd);

            for (com.allvens.allworkouts.data_manager.database.WorkoutInfo workout : allWorkouts) {
                // Skip disabled workouts
                if (!enabledNames.contains(workout.getWorkout().toLowerCase())) continue;

                int progress = workout.getProgress();
                boolean doneToday = completedTodayIds.contains(workout.getId());

                int sessionsUntilMax;
                if (progress >= 8 && !doneToday) {
                    // Already at max day — today IS the max day
                    sessionsUntilMax = 0;
                } else {
                    // Project +1 only if today's workout hasn't been completed yet
                    int projectedProgress = progress + (doneToday ? 0 : 1);
                    sessionsUntilMax = 8 - projectedProgress;
                }

                if (sessionsUntilMax >= 0) {
                    int maxDay = todayDay + sessionsUntilMax;
                    if (maxDay >= todayDay && maxDay <= daysInMonth) {
                        maxWorkoutDays.put(maxDay, true);
                        if (!maxWorkoutNames.containsKey(maxDay)) {
                            maxWorkoutNames.put(maxDay, new java.util.ArrayList<String>());
                        }
                        maxWorkoutNames.get(maxDay).add(workout.getWorkout());
                        Log.d(TAG, workout.getWorkout() + " max day on day " + maxDay +
                                " (" + sessionsUntilMax + " sessions away, progress=" + progress +
                                ", doneToday=" + doneToday + ")");
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calculating max workout days", e);
        }
    }

    /**
     * Get the count of unique workout types completed on a specific day
     */
    private int getUniqueWorkoutCountForDay(WorkoutWrapper wrapper, Calendar day) {
        // Get start and end timestamps for the day (in seconds)
        Calendar dayStart = (Calendar) day.clone();
        dayStart.set(Calendar.HOUR_OF_DAY, 0);
        dayStart.set(Calendar.MINUTE, 0);
        dayStart.set(Calendar.SECOND, 0);
        dayStart.set(Calendar.MILLISECOND, 0);
        long startTimestamp = dayStart.getTimeInMillis() / 1000;

        Calendar dayEnd = (Calendar) day.clone();
        dayEnd.set(Calendar.HOUR_OF_DAY, 23);
        dayEnd.set(Calendar.MINUTE, 59);
        dayEnd.set(Calendar.SECOND, 59);
        dayEnd.set(Calendar.MILLISECOND, 999);
        long endTimestamp = dayEnd.getTimeInMillis() / 1000;

        return wrapper.getUniqueWorkoutIdsForDay(startTimestamp, endTimestamp).size();
    }
    
    /**
     * Calculate current streak and max streak from workout data
     */
    private void calculateStreaks() {
        // Reset streaks
        currentStreak = 0;
        maxStreak = 0;
        int tempStreak = 0;
        
        // Calculate from today backwards for current streak
        Calendar today = Calendar.getInstance();
        Calendar checkDay = (Calendar) today.clone();
        
        // Only calculate if we're viewing the current month
        if (todayDay > 0) {
            // Count backwards from today to find current streak
            for (int i = todayDay; i >= 1; i--) {
                Integer count = workoutCounts.get(i);
                if (count != null && count > 0) {
                    currentStreak++;
                } else {
                    break; // Streak broken
                }
            }
        }
        
        // Calculate max streak for the visible month
        for (int day = 1; day <= daysInMonth; day++) {
            Integer count = workoutCounts.get(day);
            if (count != null && count > 0) {
                tempStreak++;
                maxStreak = Math.max(maxStreak, tempStreak);
            } else {
                tempStreak = 0;
            }
        }
        
        Log.d(TAG, "Current streak: " + currentStreak + ", Max streak: " + maxStreak);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Ensure calendar data is up-to-date for proper weeks calculation
        updateCalendarData();
        
        // Calculate header heights with more generous spacing
        monthHeaderHeight = getResources().getDimensionPixelSize(R.dimen.spacing_5); // 32dp
        headerHeight = getResources().getDimensionPixelSize(R.dimen.spacing_4); // 24dp
        
        // More generous padding around the calendar
        int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // 16dp padding from edges
        gridStartX = horizontalPadding;
        float availableWidth = w - (horizontalPadding * 2);
        
        // Calculate cell size dynamically to fill width
        cellGap = getResources().getDimensionPixelSize(R.dimen.heat_cell_gap);
        float totalGapWidth = cellGap * 6; // 6 gaps between 7 cells
        cellSize = (availableWidth - totalGapWidth) / 7; // 7 cells
        
        // More generous spacing between header and calendar grid
        int spacingBetweenHeaderAndGrid = getResources().getDimensionPixelSize(R.dimen.spacing_4); // 24dp
        gridStartY = monthHeaderHeight + headerHeight + spacingBetweenHeaderAndGrid;
        
        Log.d(TAG, "Dynamic grid: " + w + "x" + h + ", cell: " + cellSize + ", gap: " + cellGap + ", start: (" + gridStartX + ", " + gridStartY + ")");
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (gridStartX <= 0 || gridStartY <= 0) return;
        
        // Save canvas state before any transformations
        canvas.save();
        
        // Add top padding for visual breathing room
        int topVisualPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // 16dp top padding
        canvas.translate(0, topVisualPadding);
        
        // Draw month/year header
        drawMonthHeader(canvas);
        
        // Draw day headers (S M T W T F S)
        drawDayHeaders(canvas);
        
        // Draw modern heat-map grid
        drawHeatMapGrid(canvas);
        
        // Restore canvas state
        canvas.restore();
    }
    
    private void drawMonthHeader(Canvas canvas) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
        
        String monthText = monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR);
        
        // Calculate positioning with proper spacing
        int centerY = monthHeaderHeight / 2;
        monthHeaderPaint.getTextBounds(monthText, 0, monthText.length(), textBounds);
        int monthTextY = centerY + (textBounds.height() / 2);
        
        // Draw subtle glow effect behind text for modern look
        monthHeaderPaint.setShadowLayer(6, 0, 2, 0x40FFFFFF);
        
        // Always center the month text properly
        int centerX = getWidth() / 2;
        canvas.drawText(monthText, centerX, monthTextY, monthHeaderPaint);
        
        // Clear shadow for other text
        monthHeaderPaint.clearShadowLayer();
    }
    
    private void drawDayHeaders(Canvas canvas) {
        float y = monthHeaderHeight + (headerHeight / 2f);
        
        for (int i = 0; i < 7; i++) {
            float x = gridStartX + (i * (cellSize + cellGap)) + (cellSize / 2f);
            
            headerPaint.getTextBounds(DAYS[i], 0, DAYS[i].length(), textBounds);
            float textY = y + (textBounds.height() / 2f);
            
            canvas.drawText(DAYS[i], x, textY, headerPaint);
        }
    }
    
    /**
     * Draw the modern heat-map grid with square cells and intensity colors
     */
    private void drawHeatMapGrid(Canvas canvas) {
        int currentDay = 1;
        
        for (int week = 0; week < weeksToShow; week++) {
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                int dayToShow = -1;
                
                // Calculate which day to show in this cell
                if (week == 0) {
                    if (dayOfWeek >= firstDayOfWeek && currentDay <= daysInMonth) {
                        dayToShow = currentDay++;
                    }
                } else {
                    if (currentDay <= daysInMonth) {
                        dayToShow = currentDay++;
                    }
                }
                
                if (dayToShow > 0) {
                    drawHeatCell(canvas, dayOfWeek, week, dayToShow);
                }
            }
        }
    }
    
    /**
     * Draw a single heat-map cell with modern design and visual effects
     */
    private void drawHeatCell(Canvas canvas, int dayOfWeek, int week, int day) {
        // Calculate cell position
        float cellX = gridStartX + (dayOfWeek * (cellSize + cellGap));
        float cellY = gridStartY + (week * (cellSize + cellGap));

        // Get workout intensity for this day
        Integer workoutCount = workoutCounts.get(day);
        int count = (workoutCount != null) ? workoutCount : 0;
        int intensityLevel = getIntensityLevel(count);

        // Check if this is a rest day (first non-workout day after a workout day)
        boolean isRest = isRestDay(day);

        // Draw subtle shadow for depth (modern effect)
        if (intensityLevel > 0 || isRest) {
            cellPaint.setColor(0x20000000); // Subtle shadow
            canvas.drawRoundRect(cellX + 2, cellY + 2, cellX + cellSize + 2, cellY + cellSize + 2, cellRadius, cellRadius, cellPaint);
        }

        // Draw the heat cell background with modern colors
        if (isRest) {
            cellPaint.setColor(colorRestDay); // Blue for rest days
        } else {
            cellPaint.setColor(heatColors[intensityLevel]);
        }
        canvas.drawRoundRect(cellX, cellY, cellX + cellSize, cellY + cellSize, cellRadius, cellRadius, cellPaint);
        
        // Draw today's outline (ring)
        if (day == todayDay) {
            strokePaint.setColor(colorTodayStroke);
            canvas.drawRoundRect(cellX - 1, cellY - 1, cellX + cellSize + 1, cellY + cellSize + 1, cellRadius, cellRadius, strokePaint);
        }
        
        // Draw max workout day outline (yellow/orange border)
        Boolean isMaxDay = maxWorkoutDays.get(day);
        if (isMaxDay != null && isMaxDay) {
            strokePaint.setColor(colorMaxDayStroke);
            strokePaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_standard) * 2); // Thicker border
            canvas.drawRoundRect(cellX - 2, cellY - 2, cellX + cellSize + 2, cellY + cellSize + 2, cellRadius, cellRadius, strokePaint);
            strokePaint.setStrokeWidth(getResources().getDimensionPixelSize(R.dimen.stroke_standard)); // Reset
        }
        
        // Draw selected outline (if interactive selection is implemented)
        if (day == selectedDay) {
            strokePaint.setColor(colorSelectedStroke);
            canvas.drawRoundRect(cellX - 2, cellY - 2, cellX + cellSize + 2, cellY + cellSize + 2, cellRadius, cellRadius, strokePaint);
        }
        
        // Draw day number
        String dayText = String.valueOf(day);
        textPaint.getTextBounds(dayText, 0, dayText.length(), textBounds);
        
        float textX = cellX + (cellSize / 2f);
        float textY = cellY + (cellSize / 2f) + (textBounds.height() / 2f);
        
        // Use optimal contrast text color based on intensity
        if (isRest) {
            textPaint.setColor(colorTextPrimary); // White text on blue rest day
        } else if (intensityLevel == 0) {
            textPaint.setColor(colorTextSecondary); // Gray text on empty cells
        } else if (intensityLevel >= 4) { // Max intensity (100% lime)
            textPaint.setColor(getResources().getColor(R.color.on_accent_primary)); // Dark text on bright lime
        } else if (intensityLevel >= 3) { // High intensity
            textPaint.setColor(colorTextPrimary); // White text on medium lime
        } else {
            textPaint.setColor(colorTextPrimary); // White text on low intensity
        }
        
        canvas.drawText(dayText, textX, textY, textPaint);
    }
    
    /**
     * Map workout count to intensity level for heat-map colors
     * Based on percentage of total workout types completed
     * @param count Number of unique workout types completed
     * @return Intensity level index (0-4) for heatColors array
     */
    private int getIntensityLevel(int count) {
        if (count == 0 || totalWorkoutTypes == 0) {
            return 0; // Empty - gray
        }

        // Calculate percentage of workout types completed
        float percentage = (float) count / totalWorkoutTypes;

        if (percentage >= 1.0f) {
            return 4; // 100% - all workouts complete
        } else if (percentage >= 0.75f) {
            return 3; // 75-99%
        } else if (percentage >= 0.5f) {
            return 2; // 50-74%
        } else if (percentage > 0) {
            return 1; // 1-49%
        } else {
            return 0; // Empty
        }
    }

    /**
     * Check if a day is a "rest day" - first non-workout day after a workout day
     * Only the first consecutive day without workouts is considered a rest day.
     * Subsequent consecutive days without workouts are "skip days" (shown as empty).
     *
     * Rules:
     * - Must be a past day (not today or future)
     * - Must have no workouts
     * - Previous day must have had workouts
     *
     * @param day The day of month to check
     * @return true if this is a rest day
     */
    private boolean isRestDay(int day) {
        // Can't be a rest day if it's today or in the future
        if (todayDay > 0 && day >= todayDay) {
            return false;
        }

        // Must have no workouts on this day
        Integer count = workoutCounts.get(day);
        if (count != null && count > 0) {
            return false; // Has workouts, not a rest day
        }

        // Check the previous day
        if (day == 1) {
            // First day of month - check if previous month's last day had workouts
            // For simplicity, we'll consider day 1 as a potential rest day if there's
            // workout history (could enhance to check previous month)
            return hasAnyWorkoutHistory();
        }

        // Check if previous day had workouts
        Integer previousDayCount = workoutCounts.get(day - 1);
        return previousDayCount != null && previousDayCount > 0;
    }

    /**
     * Check if there's any workout history in the current month
     * Used to determine if the first day could be a rest day
     */
    private boolean hasAnyWorkoutHistory() {
        return !workoutCounts.isEmpty();
    }
    
    /**
     * Refresh the calendar with current data
     */
    public void refreshCalendar() {
        updateCalendarData();
        loadWorkoutData();
        invalidate(); // Trigger redraw
    }

    /**
     * Count total complete sessions (days where ALL workouts were completed)
     * Scans all history, not just current month
     * @return Number of days with all workouts completed
     */
    public int getCompleteSessions() {
        int completeSessions = 0;
        WorkoutWrapper workoutWrapper = new WorkoutWrapper(getContext());

        try {
            workoutWrapper.open();

            // Get the number of workout types that exist in the database
            List<com.allvens.allworkouts.data_manager.database.WorkoutInfo> allWorkouts =
                workoutWrapper.getAllWorkouts();
            int workoutTypeCount = allWorkouts.size();

            if (workoutTypeCount == 0) {
                workoutWrapper.close();
                return 0;
            }

            // Collect all unique dates with unique workout IDs
            Map<String, java.util.Set<Long>> dateToWorkoutIds = new HashMap<>();

            for (com.allvens.allworkouts.data_manager.database.WorkoutInfo workout : allWorkouts) {
                List<com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo> history =
                    workoutWrapper.getHistoryForWorkout(workout.getId());

                for (com.allvens.allworkouts.data_manager.database.WorkoutHistoryInfo entry : history) {
                    long completionDate = entry.getCompletionDate();
                    if (completionDate > 0) {
                        // Convert to date key (YYYY-MM-DD)
                        Calendar cal = Calendar.getInstance();
                        cal.setTimeInMillis(completionDate * 1000);
                        String dateKey = cal.get(Calendar.YEAR) + "-" +
                                        cal.get(Calendar.MONTH) + "-" +
                                        cal.get(Calendar.DAY_OF_MONTH);

                        if (!dateToWorkoutIds.containsKey(dateKey)) {
                            dateToWorkoutIds.put(dateKey, new java.util.HashSet<>());
                        }
                        dateToWorkoutIds.get(dateKey).add(workout.getId());
                    }
                }
            }

            // Count days where all workout types were completed
            for (java.util.Set<Long> workoutIds : dateToWorkoutIds.values()) {
                if (workoutIds.size() >= workoutTypeCount) {
                    completeSessions++;
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error counting complete sessions", e);
        } finally {
            workoutWrapper.close();
        }

        return completeSessions;
    }
    
    /**
     * Force immediate calendar refresh (for debugging)
     */
    public void forceRefresh() {
        updateCalendarData();
        loadWorkoutData();
        requestLayout();
        invalidate();
    }
    
    /**
     * Set the calendar to show a specific month/year
     * @param calendar Calendar instance set to desired month/year
     */
    public void setMonth(Calendar calendar) {
        this.calendar = (Calendar) calendar.clone();
        updateCalendarData();
        loadWorkoutData();
        invalidate();
    }
    
    /**
     * Set selected day for interactive highlighting (future feature)
     * @param day Day of month to select (1-31) or -1 to clear selection
     */
    public void setSelectedDay(int day) {
        this.selectedDay = day;
        invalidate();
    }
    
    /**
     * Test method to verify all dates are properly calculated and positioned
     * This helps debug if any dates are being cut off
     */
    public void debugCalendarLayout() {
        int currentDay = 1;
        Log.d(TAG, "=== Calendar Layout Debug ===");
        Log.d(TAG, "WeeksToShow: " + weeksToShow + ", DaysInMonth: " + daysInMonth + ", FirstDayOfWeek: " + firstDayOfWeek);
        
        for (int week = 0; week < weeksToShow; week++) {
            StringBuilder weekStr = new StringBuilder("Week " + week + ": ");
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                int dayToShow = -1;
                
                if (week == 0) {
                    if (dayOfWeek >= firstDayOfWeek && currentDay <= daysInMonth) {
                        dayToShow = currentDay++;
                    }
                } else {
                    if (currentDay <= daysInMonth) {
                        dayToShow = currentDay++;
                    }
                }
                
                if (dayToShow > 0) {
                    weekStr.append(dayToShow).append(" ");
                } else {
                    weekStr.append("- ");
                }
            }
            Log.d(TAG, weekStr.toString());
        }
        
        if (currentDay <= daysInMonth) {
            Log.w(TAG, "WARNING: Days " + currentDay + " to " + daysInMonth + " were not positioned! Need more weeks.");
        } else {
            Log.d(TAG, "SUCCESS: All " + daysInMonth + " days positioned correctly.");
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        // Ensure we have enough weeks calculated and up-to-date cell size
        updateCalendarData();
        
        // Recalculate cell dimensions based on current width
        int horizontalPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // 16dp padding from edges
        float availableWidth = width - (horizontalPadding * 2);
        cellGap = getResources().getDimensionPixelSize(R.dimen.heat_cell_gap);
        float totalGapWidth = cellGap * 6; // 6 gaps between 7 cells
        cellSize = (availableWidth - totalGapWidth) / 7; // 7 cells
        
        // Calculate required height to show all content properly
        float totalGridHeight = (cellSize * weeksToShow) + (cellGap * (weeksToShow - 1));
        int headerSpace = monthHeaderHeight + headerHeight;
        int spacingBetweenHeaderAndGrid = getResources().getDimensionPixelSize(R.dimen.spacing_4); // Match gridStartY calculation
        int topPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // Top padding within calendar view
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // Bottom padding within calendar view
        
        int requiredHeight = (int) (totalGridHeight + headerSpace + spacingBetweenHeaderAndGrid + topPadding + bottomPadding);
        
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            // Use specified height but warn if it's not adequate
            height = heightSize;
            if (heightSize < requiredHeight) {
                Log.w(TAG, "Calendar height constrained to " + heightSize + "dp, but requires " + requiredHeight + "dp. Dates may be cut off.");
            }
        } else {
            // Use calculated height
            height = requiredHeight;
            
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
                if (height < requiredHeight) {
                    Log.w(TAG, "Calendar height limited to " + height + "dp, but requires " + requiredHeight + "dp. Dates may be cut off.");
                }
            }
        }
        
        Log.d(TAG, "Calendar measure: " + width + "x" + height + " (required: " + requiredHeight + ", weeks: " + weeksToShow + ", cellSize: " + cellSize + ")");
        setMeasuredDimension(width, height);
    }
}