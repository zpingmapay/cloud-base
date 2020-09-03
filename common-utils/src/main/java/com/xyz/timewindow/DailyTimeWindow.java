package com.xyz.timewindow;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;

import static com.xyz.timewindow.TimePoint.BEGIN_OF_DAY;
import static com.xyz.timewindow.TimePoint.END_OF_DAY;

/**
 * e.g. 7:00 ~ 19:00, everyday
 */
class DailyTimeWindow implements RecurringTimeWindow {
    private final TimePoint start;
    private final TimePoint end;

    DailyTimeWindow(int startHour, int startMinute, int endHour, int endMinute) {
        this.start = new TimePoint(startHour, startMinute);
        this.end = new TimePoint(endHour, endMinute);
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
            return (timePoint.compareTo(this.start) >= 0 && timePoint.compareTo(END_OF_DAY) <= 0)
                    || (timePoint.compareTo(BEGIN_OF_DAY) >= 0 && timePoint.compareTo(this.end) < 0);
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
        LocalDateTime startTime = localDateTime.withHour(this.start.hour).withMinute(this.start.minute).withSecond(0).withNano(0);
        long startInMillis = startTime.toInstant(ZoneOffset.of(DEFAULT_ZONE_OFFSET)).toEpochMilli();
        while (startInMillis <= timestamp) {
            startInMillis += DAY_IN_MILLIS;
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
        LocalDateTime endTime = localDateTime.withHour(this.end.hour).withMinute(this.end.minute).withSecond(0).withNano(0);
        long endInMillis = endTime.toInstant(ZoneOffset.of(DEFAULT_ZONE_OFFSET)).toEpochMilli();
        while (endInMillis <= timestamp) {
            endInMillis += DAY_IN_MILLIS;
        }
        return endInMillis;
    }

    @Override
    public long getNextEndTime() {
        return this.getNextEndTime(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "DailyTimeWindow{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean isValid() {
        if (!this.start.isValid() || !this.end.isValid()) {
            return false;
        }

        return this.start.compareTo(this.end) != 0;
    }
}
