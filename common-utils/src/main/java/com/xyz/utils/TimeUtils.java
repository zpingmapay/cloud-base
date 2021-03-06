package com.xyz.utils;

import com.xyz.exception.CommonException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.Objects;

public class TimeUtils {
    public static final String DATE_MIN_STR = "yyyyMMdd";
    public static final String DATE_SMALL_STR = "yyyy-MM-dd";
    public static final String DATE_FULL_STR = "yyyy-MM-dd HH:mm:ss";
    public static final String SHORT_YEAR_DATE_MIN_STR_TIME = "yyMMddHHmmss";
    public static final String TIME_FULL_STR = "yyyyMMddHHmmss";
    public static final String DATE_FULL_STR_BIG_ALL = "yyyyMMddHHmmssSSS";
    public static final String DATE_FULL_STR_MIN_YEAR = "yyMMddHHmmss";
    public static final String DATE_TIME_MIN_STR = "HH:mm:ss";
    public static final String TIME_HOUR_MINUTE_STR = "HH:mm";

    public static Date parse(String strDate, String pattern) {
        SimpleDateFormat df = new SimpleDateFormat(pattern);
        try {
            return df.parse(strDate);
        } catch (ParseException e) {
            throw new CommonException(null, e.getMessage(), e);
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

    /**
     * @return 秒级时间戳
     */
    public static long getSecondTimestamp(Date date) {
        return date.toInstant().getEpochSecond();
    }

    /**
     * @return 当前时间秒级时间戳
     */
    public static long currentSecondTimestamp() {
        return Instant.now().getEpochSecond();
    }

    public static int dayOfMonth(long timestamp) {
        return toLocalDateTime(new Date(timestamp)).getDayOfMonth();
    }

    /**
     * 获取指定时间所在月的开始时间
     */
    public static Date startTimeOfMonth(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDate(date).with(TemporalAdjusters.firstDayOfMonth()));
    }

    /**
     * 获取指定时间所在年的开始时间
     */
    public static Date startTimeOfYear(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDate(date).with(TemporalAdjusters.firstDayOfYear()));
    }

    /**
     * 获取指定时间所在年的开始时间
     */
    public static Date startTimeOfDay(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDate(date));
    }

    /**
     * +years
     */
    public static Date addYears(Date date, long years) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusYears(years));
    }

    /**
     * +month
     */
    public static Date addMonths(Date date, long months) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusMonths(months));
    }

    /**
     * +day
     */
    public static Date addDays(Date date, long days) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusDays(days));
    }

    /**
     * +hour
     */
    public static Date addHours(Date date, long hours) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusHours(hours));
    }

    /**
     * +minute
     */
    public static Date addMinutes(Date date, long minutes) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusMinutes(minutes));
    }

    /**
     * +second
     */
    public static Date addSeconds(Date date, long seconds) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusSeconds(seconds));
    }

    /**
     * +week
     */
    public static Date addWeeks(Date date, long weeks) {
        if (Objects.isNull(date)) {
            return null;
        }
        return toDate(toLocalDateTime(date).plusWeeks(weeks));
    }

    public static LocalDate toLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date toDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

}
