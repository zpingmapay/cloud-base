package com.xyz.timewindow;

import com.xyz.utils.TimeUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Recurring time window, e.g.
 * 7:00 ~ 19:00, everyday
 * 19:00 Friday ~ 7:00 Monday, every week
 */
public interface RecurringTimeWindow {
    long HOUR_IN_MILLIS = 60 * 60 * 1000L;
    long DAY_IN_MILLIS = 24 * HOUR_IN_MILLIS;
    long WEEK_IN_MILLIS = 7 * DAY_IN_MILLIS;
    String DEFAULT_ZONE_OFFSET = "+8";

    boolean isValid();

    boolean isInTimeWindow(long timestamp);

    boolean isInTimeWindow();

    long getNextStartTime(long timestamp);

    long getNextStartTime();

    long getNextEndTime(long timestamp);

    long getNextEndTime();

    default boolean conflictWith(RecurringTimeWindow timeWindow) {
        if (!this.getClass().equals(timeWindow.getClass())) {
            return false;
        }
        return conflictWith(this, timeWindow);
    }

    static boolean conflictWith(RecurringTimeWindow timeWindow1, RecurringTimeWindow timeWindow2) {
        long now = System.currentTimeMillis();
        long nextStartTime = timeWindow1.getNextStartTime(now);
        long nextEndTime = timeWindow1.getNextEndTime(now);
        if ((timeWindow2.isInTimeWindow(nextStartTime) && !isEndBoundary(timeWindow2, nextStartTime))
                || (timeWindow2.isInTimeWindow(nextEndTime) && !isStartBoundary(timeWindow2, nextEndTime))) {
            return true;
        }

        nextStartTime = timeWindow2.getNextStartTime(now);
        nextEndTime = timeWindow2.getNextEndTime(now);
        return (timeWindow1.isInTimeWindow(nextStartTime) && !isEndBoundary(timeWindow1, nextStartTime))
                || (timeWindow1.isInTimeWindow(nextEndTime) && !isStartBoundary(timeWindow1, nextEndTime));
    }

    static boolean isStartBoundary(RecurringTimeWindow timeWindow, long timestamp) {
        return !timeWindow.isInTimeWindow(timestamp - 1) && timeWindow.isInTimeWindow(timestamp + 1);
    }

    static boolean isEndBoundary(RecurringTimeWindow timeWindow, long timestamp) {
        return timeWindow.isInTimeWindow(timestamp - 1) && !timeWindow.isInTimeWindow(timestamp + 1);
    }

    /**
     * Create daily recurring time window instance
     *
     * @param start: 00:00 ~ 23:59
     * @param end:   00:00 ~ 23:59
     * @return a daily time window
     */
    static RecurringTimeWindow createDaily(String start, String end) {
        LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern(TimeUtils.TIME_HOUR_MINUTE_STR));
        LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern(TimeUtils.TIME_HOUR_MINUTE_STR));
        return createDaily(startTime.getHour(), startTime.getMinute(), endTime.getHour(), endTime.getMinute());
    }

    /**
     * Create daily recurring time window instance.
     *
     * @param startHour:   0 ~ 23
     * @param startMinute: 0 ~ 59
     * @param endHour:     0 ~ 23
     * @param endMinute:   0 ~ 59
     * @return a daily time window
     */
    static RecurringTimeWindow createDaily(int startHour, int startMinute, int endHour, int endMinute) {
        DailyTimeWindow dailyTimeWindow = new DailyTimeWindow(startHour, startMinute, endHour, endMinute);
        if (!dailyTimeWindow.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + dailyTimeWindow.toString());
        }
        return dailyTimeWindow;
    }

    /**
     * Create weekly recurring time window instance.
     *
     * @param startOfWeek: 1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @param start:       00:00 ~ 23:59
     * @param endOfWeek:   1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @param end:         00:00 ~ 23:59
     * @return a weekly time window
     */
    static RecurringTimeWindow createWeekly(int startOfWeek, String start, int endOfWeek, String end) {
        LocalTime startTime = LocalTime.parse(start, DateTimeFormatter.ofPattern(TimeUtils.TIME_HOUR_MINUTE_STR));
        LocalTime endTime = LocalTime.parse(end, DateTimeFormatter.ofPattern(TimeUtils.TIME_HOUR_MINUTE_STR));
        return createWeekly(startOfWeek, startTime.getHour(), startTime.getMinute(), endOfWeek, endTime.getHour(), endTime.getMinute());
    }

    /**
     * Create weekly recurring time window instance.
     *
     * @param startOfWeek: 1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @param startHour:   0 ~ 23
     * @param startMinute: 0 ~ 59
     * @param endOfWeek:   1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @param endHour:     0 ~ 23
     * @param endMinute:   0 ~ 59
     * @return a weekly time window
     */
    static RecurringTimeWindow createWeekly(int startOfWeek, int startHour, int startMinute, int endOfWeek, int endHour, int endMinute) {
        WeeklyTimeWindow weeklyTimeWindow = new WeeklyTimeWindow(startOfWeek, startHour, startMinute, endOfWeek, endHour, endMinute);
        if (!weeklyTimeWindow.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + weeklyTimeWindow.toString());
        }
        return weeklyTimeWindow;
    }

    /**
     * Create weekly recurring time window instance.
     *
     * @param startOfWeek: 1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @param endOfWeek:   1 ~ 7: 1 - Monday, 2 - Tuesday, 3 - Wednesday, 4 - Thursday, 5 - Friday, 6 - Saturday, 7 - Sunday
     * @return a weekly time window from 00:00 of {@code startOfWeek} to 23:59 of {@code endOfWeek}
     */
    static RecurringTimeWindow createWeekly(int startOfWeek, int endOfWeek) {
        WeeklyTimeWindow weeklyTimeWindow = new WeeklyTimeWindow(startOfWeek, endOfWeek);
        if (!weeklyTimeWindow.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + weeklyTimeWindow.toString());
        }
        return weeklyTimeWindow;
    }

    static RecurringTimeWindow createMonthly(int[] days) {
        MonthlyTimeWindow monthlyTimeWindow = new MonthlyTimeWindow(days);
        if (!monthlyTimeWindow.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + monthlyTimeWindow.toString());
        }
        return monthlyTimeWindow;
    }

    @Deprecated
    static boolean conflictWithList(List<RecurringTimeWindow> timeWindowList) {
        if (CollectionUtils.isEmpty(timeWindowList)) {
            return false;
        }
        for (int i = 0; i < timeWindowList.size(); i++) {
            for (int j = i + 1; j < timeWindowList.size(); j++) {
                RecurringTimeWindow timeWindow1 = timeWindowList.get(i);
                RecurringTimeWindow timeWindow2 = timeWindowList.get(j);
                if (timeWindow1.conflictWith(timeWindow2)) {
                    return true;
                }
            }
        }
        return false;
    }
}
