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
    
    // Colors for different workout intensities
    private int colorBackground;
    private int colorNoWorkout;
    private int colorOneWorkout;
    private int colorAllWorkouts; 
    private int colorTextPrimary;
    private int colorTextSecondary;
    
    // Calendar data
    private Calendar calendar;
    private Map<Integer, Integer> workoutCounts; // day -> workout count
    private int daysInMonth;
    private int firstDayOfWeek;
    private int totalWorkoutTypes = 4; // Pull-ups, Push-ups, Sit-ups, Squats
    private int currentStreak = 0;
    private int maxStreak = 0;
    private int todayDay = -1;
    
    // Drawing objects
    private Paint textPaint;
    private Paint dayBackgroundPaint;
    private Paint headerPaint;
    private Paint monthHeaderPaint;
    private Paint todayOutlinePaint;
    private Paint streakLinePaint;
    
    // Dimensions
    private int cellWidth;
    private int cellHeight;
    private int headerHeight;
    private int monthHeaderHeight;
    private Rect textBounds = new Rect();
    
    // Calendar grid
    private static final String[] DAYS = {"S", "M", "T", "W", "T", "F", "S"};
    private int weeksToShow = 5; // Will be calculated dynamically
    
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
        // Load colors from resources (modern color system)
        colorBackground = context.getResources().getColor(R.color.surface_variant);
        colorNoWorkout = context.getResources().getColor(R.color.objects); // Gray (keeping existing)
        colorOneWorkout = context.getResources().getColor(R.color.secondary); // Teal for partial workouts
        colorAllWorkouts = context.getResources().getColor(R.color.primary); // Crimson for full workouts
        colorTextPrimary = context.getResources().getColor(R.color.selectedButton);
        colorTextSecondary = context.getResources().getColor(R.color.unSelectedButton);
        
        // Initialize calendar
        calendar = Calendar.getInstance();
        workoutCounts = new HashMap<>();
        updateCalendarData();
        
        // Initialize paints
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(colorTextPrimary);
        textPaint.setTextSize(context.getResources().getDimensionPixelSize(R.dimen.text_size_caption)); // Back to readable size
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        dayBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
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
        
        todayOutlinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        todayOutlinePaint.setColor(colorTextPrimary);
        todayOutlinePaint.setStyle(Paint.Style.STROKE);
        todayOutlinePaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.spacing_1) + 1); // Thicker stroke
        
        streakLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        streakLinePaint.setColor(colorAllWorkouts);
        streakLinePaint.setStrokeWidth(context.getResources().getDimensionPixelSize(R.dimen.spacing_1) + 1); // Slightly thicker
        streakLinePaint.setStrokeCap(Paint.Cap.ROUND);
        streakLinePaint.setAlpha(200); // Slightly transparent for subtlety
        
        // Load workout data
        loadWorkoutData();
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
        // Minimum 5 weeks to avoid layout jumps
        weeksToShow = Math.max(5, weeksToShow);
        
        Log.d(TAG, "Month: " + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR));
        Log.d(TAG, "Days in month: " + daysInMonth + ", First day: " + firstDayOfWeek + ", Today: " + todayDay + ", Weeks: " + weeksToShow);
    }
    
    private void loadWorkoutData() {
        workoutCounts.clear();
        
        WorkoutWrapper workoutWrapper = new WorkoutWrapper(getContext());
        try {
            workoutWrapper.open();
            
            // For each day in the current month, count workouts
            Calendar dayCalendar = (Calendar) calendar.clone();
            
            for (int day = 1; day <= daysInMonth; day++) {
                dayCalendar.set(Calendar.DAY_OF_MONTH, day);
                int workoutCount = getWorkoutCountForDay(workoutWrapper, dayCalendar);
                if (workoutCount > 0) {
                    workoutCounts.put(day, workoutCount);
                    Log.d(TAG, "Day " + day + ": " + workoutCount + " workouts");
                }
            }
            
            // Calculate streaks
            calculateStreaks();
            
        } catch (Exception e) {
            Log.e(TAG, "Error loading workout data", e);
        } finally {
            workoutWrapper.close();
        }
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
    
    private int getWorkoutCountForDay(WorkoutWrapper wrapper, Calendar day) {
        // TODO: In future version, query database for actual workout history by date
        // For now, create realistic sample data to demonstrate the feature
        
        Calendar today = Calendar.getInstance();
        long dayTime = day.getTimeInMillis();
        long todayTime = today.getTimeInMillis();
        long daysDiff = (todayTime - dayTime) / (1000 * 60 * 60 * 24);
        
        // Don't show workouts for future dates
        if (dayTime > todayTime) {
            return 0;
        }
        
        // Create sample pattern: more active in recent days, less in the past
        int dayOfMonth = day.get(Calendar.DAY_OF_MONTH);
        int dayOfWeek = day.get(Calendar.DAY_OF_WEEK);
        
        // Skip some days to make it realistic (rest days)
        if (dayOfWeek == Calendar.SUNDAY && dayOfMonth % 2 == 0) {
            return 0; // Rest days on some Sundays
        }
        
        if (daysDiff == 0) {
            return 3; // Today: did most workouts
        } else if (daysDiff <= 3) {
            // Last 3 days: high activity
            return (dayOfMonth % 4 == 0) ? 4 : ((dayOfMonth % 3 == 0) ? 2 : 1);
        } else if (daysDiff <= 7) {
            // Last week: moderate activity
            return (dayOfMonth % 3 == 0) ? ((dayOfMonth % 2 == 0) ? 2 : 1) : 0;
        } else if (daysDiff <= 14) {
            // Two weeks ago: lower activity
            return (dayOfMonth % 4 == 0) ? 1 : 0;
        } else {
            // Older: very little activity
            return (dayOfMonth % 7 == 0 && dayOfMonth % 2 == 1) ? 1 : 0;
        }
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        // Calculate cell dimensions - reasonably compact
        cellWidth = w / 7; // 7 days per week
        monthHeaderHeight = getResources().getDimensionPixelSize(R.dimen.spacing_5); // Proper spacing
        headerHeight = getResources().getDimensionPixelSize(R.dimen.spacing_3); // Reasonable spacing
        
        // Make cells with consistent spacing between rows and columns
        int extraSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int availableHeight = h - monthHeaderHeight - headerHeight - extraSpacing;
        
        // Add row spacing to match column spacing
        // Column spacing is natural from cellWidth division, so we need more space
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3); // Increase to match visual column spacing
        int bottomPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3); // Explicit bottom padding
        int totalRowSpacing = rowSpacing * weeksToShow + bottomPadding; // Space before each week + explicit bottom padding
        
        // Calculate cell height accounting for row spacing and bottom padding
        int availableForCells = availableHeight - totalRowSpacing;
        cellHeight = availableForCells / weeksToShow;
        
        Log.d(TAG, "Size changed: " + w + "x" + h + ", cell: " + cellWidth + "x" + cellHeight);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (cellWidth <= 0 || cellHeight <= 0) return;
        
        // Draw month/year header
        drawMonthHeader(canvas);
        
        // Draw day headers (S M T W T F S)
        drawDayHeaders(canvas);
        
        // Draw calendar grid
        drawCalendarGrid(canvas);
    }
    
    private void drawMonthHeader(Canvas canvas) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June",
                              "July", "August", "September", "October", "November", "December"};
        
        String monthText = monthNames[calendar.get(Calendar.MONTH)] + " " + calendar.get(Calendar.YEAR);
        
        // Draw month/year in center
        int centerX = getWidth() / 2;
        int centerY = monthHeaderHeight / 2;
        
        monthHeaderPaint.getTextBounds(monthText, 0, monthText.length(), textBounds);
        int textY = centerY + (textBounds.height() / 2);
        
        canvas.drawText(monthText, centerX, textY, monthHeaderPaint);
        
        // Draw streak info on the right if we have streaks
        if (maxStreak > 0 && todayDay > 0) {
            String streakText = "🔥 " + maxStreak;
            
            // Position streak text on the right side
            headerPaint.getTextBounds(streakText, 0, streakText.length(), textBounds);
            int streakX = getWidth() - textBounds.width() - getResources().getDimensionPixelSize(R.dimen.spacing_2);
            int streakY = centerY + (textBounds.height() / 2);
            
            // Use accent color for streak
            headerPaint.setColor(colorAllWorkouts);
            canvas.drawText(streakText, streakX, streakY, headerPaint);
            // Reset color
            headerPaint.setColor(colorTextSecondary);
        }
    }
    
    private void drawDayHeaders(Canvas canvas) {
        // Add more spacing between month and day headers
        int y = monthHeaderHeight + getResources().getDimensionPixelSize(R.dimen.spacing_3) + (headerHeight / 2);
        
        for (int i = 0; i < 7; i++) {
            int x = (i * cellWidth) + (cellWidth / 2);
            
            headerPaint.getTextBounds(DAYS[i], 0, DAYS[i].length(), textBounds);
            int textY = y + (textBounds.height() / 2);
            
            canvas.drawText(DAYS[i], x, textY, headerPaint);
        }
    }
    
    private void drawCalendarGrid(Canvas canvas) {
        int currentDay = 1;
        int extraSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int rowSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        
        // First pass: Draw all streak lines in the background
        currentDay = 1;
        for (int week = 0; week < weeksToShow; week++) {
            // Add row spacing before ALL weeks (including first week)
            int rowY = monthHeaderHeight + headerHeight + extraSpacing + rowSpacing + (week * cellHeight) + (week * rowSpacing);
            
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                int colX = dayOfWeek * cellWidth;
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
                    drawStreakLinesForDay(canvas, colX, rowY, cellWidth, cellHeight, dayToShow, dayOfWeek);
                }
            }
        }
        
        // Second pass: Draw day cells on top
        currentDay = 1;
        for (int week = 0; week < weeksToShow; week++) {
            // Add row spacing before ALL weeks (including first week)
            int rowY = monthHeaderHeight + headerHeight + extraSpacing + rowSpacing + (week * cellHeight) + (week * rowSpacing);
            
            for (int dayOfWeek = 0; dayOfWeek < 7; dayOfWeek++) {
                int colX = dayOfWeek * cellWidth;
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
                    drawDayCell(canvas, colX, rowY, cellWidth, cellHeight, dayToShow, dayOfWeek);
                }
            }
        }
    }
    
    private void drawStreakLinesForDay(Canvas canvas, int x, int y, int width, int height, int day, int dayOfWeek) {
        // Get workout count for this day
        Integer workoutCount = workoutCounts.get(day);
        int count = (workoutCount != null) ? workoutCount : 0;
        
        // Draw streak line to next day if both days have workouts and not at end of week
        if (count > 0 && day < daysInMonth && dayOfWeek < 6) {
            Integer nextCount = workoutCounts.get(day + 1);
            if (nextCount != null && nextCount > 0) {
                // Calculate centers and radius (matching the circle drawing logic)
                int centerX = x + width / 2;
                int centerY = y + height / 2;
                int nextCellX = x + width;
                int nextCenterX = nextCellX + (width / 2);
                
                // Use consistent radius (same as circles)
                int basePadding = getResources().getDimensionPixelSize(R.dimen.spacing_1);
                int radius = Math.min(width, height) / 3 + basePadding;
                
                drawStreakLine(canvas, centerX, centerY, nextCenterX, centerY, radius);
            }
        }
    }
    
    private void drawDayCell(Canvas canvas, int x, int y, int width, int height, int day, int dayOfWeek) {
        // Get workout count for this day
        Integer workoutCount = workoutCounts.get(day);
        int count = (workoutCount != null) ? workoutCount : 0;
        
        // Calculate positions - consistent sized circles for all days
        int centerX = x + width / 2;
        int centerY = y + height / 2;
        
        // Use consistent radius with enough padding for double-digit numbers
        String dayText = String.valueOf(day);
        int basePadding = getResources().getDimensionPixelSize(R.dimen.spacing_1);
        int radius = Math.min(width, height) / 3 + basePadding; // Same size for all circles
        
        // Draw background circle based on workout activity
        int backgroundColor = getBackgroundColorForWorkoutCount(count);
        
        // For today, draw a larger background circle first to block any streak lines
        if (day == todayDay) {
            int outlineRadius = radius + getResources().getDimensionPixelSize(R.dimen.spacing_1);
            
            // Draw background circle to block streak lines
            dayBackgroundPaint.setColor(colorBackground); // Use card background color
            canvas.drawCircle(centerX, centerY, outlineRadius + 2, dayBackgroundPaint);
        }
        
        // Draw the main workout circle
        dayBackgroundPaint.setColor(backgroundColor);
        canvas.drawCircle(centerX, centerY, radius, dayBackgroundPaint);
        
        // Draw today's outline
        if (day == todayDay) {
            int outlineRadius = radius + getResources().getDimensionPixelSize(R.dimen.spacing_1);
            canvas.drawCircle(centerX, centerY, outlineRadius, todayOutlinePaint);
        }
        
        // Draw day number (dayText already declared above)
        textPaint.getTextBounds(dayText, 0, dayText.length(), textBounds);
        int textY = centerY + (textBounds.height() / 2);
        
        // Adjust text color based on background and today status
        if (day == todayDay) {
            textPaint.setColor(colorTextPrimary); // Always white for today
        } else {
            textPaint.setColor(count > 0 ? colorTextPrimary : colorTextSecondary);
        }
        
        canvas.drawText(dayText, centerX, textY, textPaint);
    }
    
    private void drawStreakLine(Canvas canvas, int startCenterX, int startCenterY, int endCenterX, int endCenterY, int radius) {
        // Draw line from edge of current circle to edge of next circle
        int lineStartX = startCenterX + radius;
        int lineEndX = endCenterX - radius;
        
        // Only draw if the line makes sense (has positive length)
        if (lineEndX > lineStartX) {
            canvas.drawLine(lineStartX, startCenterY, lineEndX, endCenterY, streakLinePaint);
        }
    }
    
    private int getBackgroundColorForWorkoutCount(int count) {
        if (count == 0) {
            return colorNoWorkout;
        } else if (count >= totalWorkoutTypes) {
            return colorAllWorkouts; // Did all workout types
        } else {
            return colorOneWorkout; // Did some workouts
        }
    }
    
    /**
     * Refresh the calendar with current data
     */
    public void refreshCalendar() {
        updateCalendarData();
        loadWorkoutData();
        invalidate(); // Trigger redraw
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        int monthHeaderSize = getResources().getDimensionPixelSize(R.dimen.spacing_4);
        int dayHeaderSize = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        int extraSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3);
        
        int height;
        if (heightMode == MeasureSpec.EXACTLY) {
            // Use all available space when constrained to fill parent
            height = heightSize;
        } else {
            // Calculate based on compact cells when wrap_content
            int cellSize = (width / 7) * 2 / 3; // Make cells 2/3 aspect ratio (wider than tall)
            int rowSpacing = getResources().getDimensionPixelSize(R.dimen.spacing_3);
            int bottomPadding = getResources().getDimensionPixelSize(R.dimen.spacing_3);
            int totalRowSpacing = rowSpacing * weeksToShow + bottomPadding; // Space before each week + explicit bottom padding
            height = (cellSize * weeksToShow) + monthHeaderSize + dayHeaderSize + extraSpacing + totalRowSpacing;
            
            if (heightMode == MeasureSpec.AT_MOST) {
                height = Math.min(height, heightSize);
            }
        }
        
        setMeasuredDimension(width, height);
    }
}