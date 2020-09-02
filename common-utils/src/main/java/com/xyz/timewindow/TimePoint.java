package com.xyz.timewindow;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;

public class TimePoint implements Comparable<TimePoint> {
    int dayOfWeek; // 0: everyday in the week, 1: Monday, 2: Tuesday: , 3, Wednesday, 4, Thursday, 5, Friday, 6, Saturday, 7, Sunday
    int hour; // 0 ~ 23
    int minute; // 0 ~ 59

    public TimePoint(int dayOfWeek, int hour, int minute) {
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.minute = minute;
    }

    public TimePoint(int hour, int minute) {
        this(0, hour, minute);
    }

    public boolean isValid() {
        return dayOfWeek >= 0 && dayOfWeek <= 7
                && hour >= 0 && hour <= 23
                && minute >= 0 && minute <= 59;
    }

    public static TimePoint valueOf(long timestamp) {
        return valueOf(new Date(timestamp));
    }

    public static TimePoint valueOf(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        int thisDayOfWeek = localDateTime.getDayOfWeek().getValue();
        int thisHour = localDateTime.getHour();
        int thisMinute = localDateTime.getMinute();
        return new TimePoint(thisDayOfWeek, thisHour, thisMinute);
    }

    @Override
    public int compareTo(TimePoint timePoint) {
        if (this.dayOfWeek == 0 || timePoint.dayOfWeek == 0 || this.dayOfWeek == timePoint.dayOfWeek) {
            if (this.hour != timePoint.hour) {
                return this.hour - timePoint.hour;
            }
            return this.minute - timePoint.minute;
        }
        return this.dayOfWeek - timePoint.dayOfWeek;
    }

    public DayOfWeek getDayOfWeek() {
        return Arrays.stream(DayOfWeek.values()).filter(x -> x.getValue() == this.dayOfWeek).findFirst().orElse(null);
    }

    @Override
    public String toString() {
        return "TimePoint{" +
                "dayOfWeek=" + dayOfWeek +
                ", hour=" + hour +
                ", minute=" + minute +
                '}';
    }
}
