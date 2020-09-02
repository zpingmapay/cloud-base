package com.xyz.timewindow;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import static com.xyz.timewindow.TimePoint.BEGIN_OF_WEEK;
import static com.xyz.timewindow.TimePoint.END_OF_WEEK;

/**
 * e.g. 19:00 Friday ~ 7:00 Monday, every week
 */
class WeeklyTimeWindow implements RecurringTimeWindow {
    private final TimePoint start;
    private final TimePoint end;

    WeeklyTimeWindow(int startOfWeek, int startHour, int startMinute, int endOfWeek, int endHour, int endMinute) {
        this.start = new TimePoint(startOfWeek, startHour, startMinute);
        this.end = new TimePoint(endOfWeek, endHour, endMinute);
    }

    @Override
    public boolean isInTimeWindow(long timestamp) {
        if (!this.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + this.toString());
        }

        TimePoint timePoint = TimePoint.valueOf(timestamp);

        if (this.start.compareTo(this.end) < 0) {
            return timePoint.compareTo(this.start) >= 0 && timePoint.compareTo(this.end) < 0;
        } else {
            return (timePoint.compareTo(this.start) >= 0 && timePoint.compareTo(END_OF_WEEK) <= 0)
                    || (timePoint.compareTo(BEGIN_OF_WEEK) >= 0 && timePoint.compareTo(this.end) < 0);
        }
    }

    @Override
    public boolean isInTimeWindow() {
        return this.isInTimeWindow(System.currentTimeMillis());
    }

    @Override
    public long getNextStartTime(long timestamp) {
        if (!this.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + this.toString());
        }

        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(timestamp).toInstant(), ZoneId.systemDefault());
        LocalDateTime startTime = localDateTime.with(TemporalAdjusters.previousOrSame(this.start.getDayOfWeek())).withHour(this.start.hour).withMinute(this.start.minute).withSecond(0).withNano(0);
        long startInMillis = startTime.toInstant(ZoneOffset.of(DEFAULT_ZONE_OFFSET)).toEpochMilli();
        while (startInMillis <= timestamp) {
            startInMillis += WEEK_IN_MILLIS;
        }
        return startInMillis;
    }

    @Override
    public long getNextStartTime() {
        return this.getNextStartTime(System.currentTimeMillis());
    }

    @Override
    public long getNextEndTime(long timestamp) {
        if (!this.isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + this.toString());
        }

        LocalDateTime localDateTime = LocalDateTime.ofInstant(new Date(timestamp).toInstant(), ZoneId.systemDefault());
        LocalDateTime endTime = localDateTime.with(TemporalAdjusters.previousOrSame(this.end.getDayOfWeek())).withHour(this.end.hour).withMinute(this.end.minute).withSecond(0).withNano(0);
        long endInMillis = endTime.toInstant(ZoneOffset.of(DEFAULT_ZONE_OFFSET)).toEpochMilli();
        while (endInMillis <= timestamp) {
            endInMillis += WEEK_IN_MILLIS;
        }
        return endInMillis;
    }

    @Override
    public long getNextEndTime() {
        return this.getNextEndTime(System.currentTimeMillis());
    }

    @Override
    public boolean isValid() {
        if (!this.start.isValid() || !this.end.isValid()) {
            return false;
        }

        if (this.start.dayOfWeek == 0 || this.end.dayOfWeek == 0) {
            return false;
        }

        return this.start.compareTo(this.end) != 0;
    }

    @Override
    public String toString() {
        return "WeeklyTimeWindow{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }
}
