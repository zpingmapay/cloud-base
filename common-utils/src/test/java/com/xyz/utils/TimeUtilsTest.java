package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TimeUtilsTest {
    @Test
    public void testMillisElapsed() {
        Instant instant = Instant.now().minusMillis(100);
        long millis = TimeUtils.millisElapsed(instant);
        Assert.isTrue(millis >= 100, "elapsed millis should be greater than 100");
    }

    @Test
    public void testMinutesBefore() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = TimeUtils.minutesBefore(now,3);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long millis = TimeUtils.millisElapsed(instant);
        Assert.isTrue(millis >= 3*60*1000, "not 3 minutes ago");
    }

    @Test
    public void testMinutesBeforeNow() {
        LocalDateTime localDateTime = TimeUtils.minutesBeforeNow(3);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long millis = TimeUtils.millisElapsed(instant);
        Assert.isTrue(millis >= 3*60*1000, "not 3 minutes ago");
    }

    @Test
    public void testMinutesAfter() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesLater = TimeUtils.minutesAfter(now,3);
        Duration duration = Duration.between(now, minutesLater);
        Assert.isTrue(duration.toMinutes() == 3, "not 3 minutes later");
    }

    @Test
    public void testMinutesAfterNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesLater = TimeUtils.minutesAfterNow(3);
        Duration duration = Duration.between(now, minutesLater);
        Assert.isTrue(duration.toMinutes() == 3, "not 3 minutes later");
    }

    @Test
    public void testDaysBefore() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime localDateTime = TimeUtils.daysBefore(now,1);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long millis = TimeUtils.millisElapsed(instant);
        Assert.isTrue(millis >= 24*60*60*1000, "not 1 day ago");
    }

    @Test
    public void testDaysBeforeNow() {
        LocalDateTime localDateTime = TimeUtils.daysBeforeNow(1);
        Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        long millis = TimeUtils.millisElapsed(instant);
        Assert.isTrue(millis >= 24*60*60*1000, "not 1 day ago");
    }

    @Test
    public void testDaysAfter() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesLater = TimeUtils.daysAfter(now,1);
        Duration duration = Duration.between(now, minutesLater);
        Assert.isTrue(duration.toMinutes() == 24*60, "not 1 day later");
    }

    @Test
    public void testDaysAfterNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime minutesLater = TimeUtils.daysAfterNow(1);
        Duration duration = Duration.between(now, minutesLater);
        Assert.isTrue(duration.toMinutes() == 24*60, "not 1 day later");
    }

    @Test
    public void testToDate() {
        LocalDateTime now = LocalDateTime.now();
        Date today = new Date();
        Date date = TimeUtils.toDate(now);
        String format1 = TimeUtils.format(today, TimeUtils.DATE_FULL_STR);
        String format2 = TimeUtils.format(date, TimeUtils.DATE_FULL_STR);
        Assert.isTrue(format1.equals(format2), "date convert failed");
    }

    @Test
    public void testToLocalDateTime() {
        Date today = new Date();
        LocalDateTime now = TimeUtils.toLocalDateTime(today);
        long millis = now.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
        Assert.isTrue(millis == today.getTime(), "date time convert failed");
    }
}
