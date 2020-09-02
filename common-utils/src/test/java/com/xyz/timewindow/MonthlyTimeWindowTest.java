package com.xyz.timewindow;

import com.xyz.utils.TimeUtils;
import com.xyz.utils.ValidationUtilsTest;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Date;

public class MonthlyTimeWindowTest {
    @Test
    public void testIsValid() {
        RecurringTimeWindow window = RecurringTimeWindow.createMonthly(new int[]{3, 6, 9});
        Assert.isTrue(window.isValid(), "valid expected");

        ValidationUtilsTest.assertException((x) -> {
            RecurringTimeWindow window1 = RecurringTimeWindow.createMonthly(new int[]{6, 6, 9});
            Assert.isTrue(!window1.isValid(), "invalid expected");
        }, IllegalArgumentException.class);

        ValidationUtilsTest.assertException((x) -> {
            RecurringTimeWindow window2 = RecurringTimeWindow.createMonthly(new int[]{9, 6, 3});
            Assert.isTrue(!window2.isValid(), "invalid expected");
        }, IllegalArgumentException.class);
    }

    @Test
    public void testIsInTimeWindow() {
        RecurringTimeWindow window = RecurringTimeWindow.createMonthly(new int[]{3, 6, 9});

        Date date = TimeUtils.parse("2019-03-01 19:00:00", TimeUtils.DATE_FULL_STR);
        boolean inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-01  19:00:00 is not in span");

        date = TimeUtils.parse("2019-03-03 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-02 07:00:00 is in span");

        date = TimeUtils.parse("2019-03-04 07:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-02 07:31:00 is not in span");

        date = TimeUtils.parse("2019-03-06 00:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-03 01:31:00 is in span");

        date = TimeUtils.parse("2019-03-31 06:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-04 06:31:00 is no in span");
    }

    @Test
    public void testGetNextStartTime() {
        RecurringTimeWindow window = RecurringTimeWindow.createMonthly(new int[]{3, 6, 9, 13, 16, 19, 23, 26, 29});

        Date date = TimeUtils.parse("2019-03-01 06:00:00", TimeUtils.DATE_FULL_STR);
        String startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-03 00:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-03 07:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-06 00:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-04 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-06 00:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-06 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-09 00:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-30 20:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-04-03 00:00:00".equals(startTime), "not the same");
    }

    @Test
    public void testGetNextEndTime() {
        RecurringTimeWindow window = RecurringTimeWindow.createMonthly(new int[]{3, 6, 9, 13, 16, 19, 23, 26, 29});

        Date date = TimeUtils.parse("2019-03-01 06:00:00", TimeUtils.DATE_FULL_STR);
        String endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-03 23:59:59".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-03 07:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-03 23:59:59".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-04 08:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-06 23:59:59".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-06 08:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-06 23:59:59".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-11 08:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-13 23:59:59".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-30 20:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-04-03 23:59:59".equals(endTime), "not the same");
    }
}
