package com.xyz.timewindow;

import com.xyz.utils.TimeUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Calendar;

/**
 * eg. 5，10，15，20，25，30 every month
 */
public class MonthlyTimeWindow implements RecurringTimeWindow {
    private final int[] days;

    MonthlyTimeWindow(int[] days) {
        this.days = days;
    }

    @Override
    public boolean isValid() {
        if (days == null || days.length == 0) {
            return false;
        }
        if (days.length == 1) {
            return true;
        }
        for (int i = 0; i < days.length - 1; i++) {
            if (days[i] >= days[i + 1]) {
                return false;
            }
        }
        if(days[days.length -1] > 31) {
            return false;
        }
        return true;
    }

    @Override
    public boolean isInTimeWindow(long timestamp) {
        if (!isValid()) {
            throw new IllegalArgumentException("Invalid time window - " + this.toString());
        }
        int day = TimeUtils.dayOfMonth(timestamp);
        return Arrays.stream(days).anyMatch(x -> x == day);
    }

    @Override
    public boolean isInTimeWindow() {
        return isInTimeWindow(System.currentTimeMillis());
    }

    @Override
    public long getNextStartTime(long timestamp) {
        int day = TimeUtils.dayOfMonth(timestamp);
        int i = 0;
        while (i < days.length) {
            if (day < days[i]) {
                return getOpenDayStart(timestamp, i);
            }
            if (day == days[i]) {
                return getNextOpenDayStart(timestamp, i);
            }
            i++;
        }
        return getNextOpenDayStart(timestamp, days.length - 1);
    }

    @Override
    public long getNextStartTime() {
        return getNextStartTime(System.currentTimeMillis());
    }

    @Override
    public long getNextEndTime(long timestamp) {
        int day = TimeUtils.dayOfMonth(timestamp);
        int i = 0;
        while (i < days.length) {
            if (day < days[i]) {
                return getOpenDayEnd(timestamp, i);
            }
            if (day == days[i]) {
                return getOpenDayEnd(timestamp, i);
            }
            i++;
        }
        return getNextOpenDayEnd(timestamp, days.length - 1);
    }

    @Override
    public long getNextEndTime() {
        return getNextEndTime(System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "MonthlyTimeWindow{" +
                "days=" + StringUtils.join(this.days, ',') +
                '}';
    }

    private long getOpenDayStart(long timestamp, int index) {
        Calendar calendar = initStartCalendar(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, days[index]);
        return calendar.getTimeInMillis();
    }

    private long getOpenDayEnd(long timestamp, int index) {
        Calendar calendar = initEndCalendar(timestamp);
        calendar.set(Calendar.DAY_OF_MONTH, days[index]);
        return calendar.getTimeInMillis();
    }

    private long getNextOpenDayStart(long timestamp, int index) {
        Calendar calendar = initStartCalendar(timestamp);
        if (index == days.length - 1) {
            calendar.set(Calendar.DAY_OF_MONTH, days[0]);
            calendar.add(Calendar.MONTH, 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, days[index + 1]);
        }
        return calendar.getTimeInMillis();
    }

    private long getNextOpenDayEnd(long timestamp, int index) {
        Calendar calendar = initEndCalendar(timestamp);
        if (index == days.length - 1) {
            calendar.set(Calendar.DAY_OF_MONTH, days[0]);
            calendar.add(Calendar.MONTH, 1);
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, days[index + 1]);
        }
        return calendar.getTimeInMillis();
    }

    private Calendar initStartCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar;
    }

    private Calendar initEndCalendar(long timestamp) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(timestamp);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar;
    }

}
