package com.xyz.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtils {
    public static final String DATE_MIN_STR = "yyyyMMdd";
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_YEAR_DATE_MIN_STR_TIME = "yyMMddHHmmss";
    public static final String DATE_FULL_STR_BIG_ALL = "yyyyMMddHHmmssSSS";
    public static final String DATE_FULL_STR_MIN_YEAR = "yyMMddHHmmss";
    public static final String DATE_TIME_MIN_STR = "HH:mm:ss";

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String format(Date date, String pattern) {
        return new SimpleDateFormat(pattern).format(date);
    }

    public static long millisElapsed(Instant start) {
        return Duration.between(start, Instant.now()).toMillis();
    }

    public static LocalDateTime daysBefore(LocalDateTime t, int days) {
        return t.minus(Duration.ofDays(days));
    }

    public static LocalDateTime daysAfter(LocalDateTime t, int days) {
        return t.plus(Duration.ofDays(days));
    }

    public static LocalDateTime minutesBefore(LocalDateTime t, int minutes) {
        return t.minus(Duration.ofMinutes(minutes));
    }

    public static LocalDateTime minutesAfter(LocalDateTime t, int minutes) {
        return t.plus(Duration.ofMinutes(minutes));
    }

    public static LocalDateTime daysBeforeNow(int days) {
        return LocalDateTime.now().minus(Duration.ofDays(days));
    }

    public static LocalDateTime daysAfterNow(int days) {
        return LocalDateTime.now().plus(Duration.ofDays(days));
    }

    public static LocalDateTime minutesBeforeNow(int minutes) {
        return LocalDateTime.now().minus(Duration.ofMinutes(minutes));
    }

    public static LocalDateTime minutesAfterNow(int minutes) {
        return LocalDateTime.now().plus(Duration.ofMinutes(minutes));
    }

    public static Date toDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDateTime toLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
}
