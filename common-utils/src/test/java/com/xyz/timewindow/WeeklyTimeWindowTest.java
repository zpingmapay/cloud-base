package com.xyz.timewindow;

import com.xyz.utils.TimeUtils;
import com.xyz.utils.ValidationUtilsTest;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Date;

public class WeeklyTimeWindowTest {
    @Test
    public void testIsValid() {
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(1, "07:00", 7, "08:00");
        Assert.isTrue(window.isValid(), "valid expected");

        window = RecurringTimeWindow.createWeekly(5, "23:59", 5, "00:00");
        Assert.isTrue(window.isValid(), "valid expected");

        ValidationUtilsTest.assertException((x) -> {
            RecurringTimeWindow window1 = RecurringTimeWindow.createWeekly(2, "07:00", 2, "07:00");
            Assert.isTrue(!window1.isValid(), "invalid expected");
        }, IllegalArgumentException.class);

        ValidationUtilsTest.assertException((x) -> {
            RecurringTimeWindow window2 = RecurringTimeWindow.createWeekly(0, "07:00", 2, "07:50");
            Assert.isTrue(!window2.isValid(), "invalid expected");
        }, IllegalArgumentException.class);
    }

    @Test
    public void testIsInTimeWindow1() {
        //Saturday 7:00 ~ Monday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(6, 7, 0, 1, 7, 0);

        Date date = TimeUtils.parse("2019-03-01 19:00:00", TimeUtils.DATE_FULL_STR);
        boolean inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-01  19:00:00 is not in span");

        date = TimeUtils.parse("2019-03-02 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-02 07:00:00 is in span");

        date = TimeUtils.parse("2019-03-02 07:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-02 07:31:00 is in span");

        date = TimeUtils.parse("2019-03-03 01:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-03 01:31:00 is in span");

        date = TimeUtils.parse("2019-03-04 06:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-04 06:31:00 is in span");

        date = TimeUtils.parse("2019-03-04 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-04 07:00:00 is not in span");

        date = TimeUtils.parse("2019-03-04 09:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan,"2019-03-04 09:00:00 is not in span");
    }

    @Test
    public void testIsInTimeWindow2() {
        //Monday 7:00 ~ Friday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(1, 7, 0, 5, 7, 0);

        Date date = TimeUtils.parse("2019-03-01 19:00:00", TimeUtils.DATE_FULL_STR);
        boolean inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-01 19:00:00 is not in span");

        date = TimeUtils.parse("2019-03-02 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-02 07:00:00 is not in span");

        date = TimeUtils.parse("2019-03-02 07:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-02 07:31:00 is not in span");

        date = TimeUtils.parse("2019-03-03 01:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-03 01:31:00 is not in span");

        date = TimeUtils.parse("2019-03-04 06:31:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "2019-03-04 06:31:00 is not in span");

        date = TimeUtils.parse("2019-03-04 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-04 07:00:00 is in span");

        date = TimeUtils.parse("2019-03-05 09:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "2019-03-05 09:00:00 is in span");
    }


    @Test
    public void testGetNextStartTime1() {
        //Monday 7:00 ~ Friday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(1, 7, 0, 5, 7, 0);

        Date date = TimeUtils.parse("2019-03-18 06:00:00", TimeUtils.DATE_FULL_STR);
        String startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-18 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-19 07:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-25 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-22 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-25 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-24 20:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-25 07:00:00".equals(startTime), "not the same");
    }

    @Test
    public void testGetNextStartTime2() {
        //Saturday 7:00 ~ Monday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(6, 7, 0, 1, 7, 0);

        Date date = TimeUtils.parse("2019-03-16 06:00:00", TimeUtils.DATE_FULL_STR);
        String startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-16 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-16 07:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-23 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-17 20:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-23 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-02-18 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-02-23 07:00:00".equals(startTime), "not the same");
    }


    @Test
    public void testGetNextEndTime1() {
        //Monday 7:00 ~ Friday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(1, 7, 0, 5, 7, 0);

        Date date = TimeUtils.parse("2019-03-18 06:00:00", TimeUtils.DATE_FULL_STR);
        String endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-22 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-22 06:59:59", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-22 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-22 07:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-29 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-22 07:00:01", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-29 07:00:00".equals(endTime), "not the same");
    }

    @Test
    public void testGetNextEndTime2() {
        //Saturday 7:00 ~ Monday 7:00 every week
        RecurringTimeWindow window = RecurringTimeWindow.createWeekly(6, 7, 0, 1, 7, 0);

        Date date = TimeUtils.parse("2019-03-18 06:00:00", TimeUtils.DATE_FULL_STR);
        String endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-18 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-16 06:59:59", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-18 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-18 07:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-25 07:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-22 07:00:01", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-25 07:00:00".equals(endTime), "not the same");
    }

    @Test
    public void testConflict1() {
        //19:30 Friday ~ 07:30 Monday every week
        RecurringTimeWindow window1 = RecurringTimeWindow.createWeekly(5, 19, 30, 1, 7, 30);

        //07:30 Monday ~ 08:30 Wednesday every weekly
        RecurringTimeWindow window2 = RecurringTimeWindow.createWeekly(1, 7, 30, 3, 8, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:30 Monday ~ 19:30 Friday every weekly
        window2 = RecurringTimeWindow.createWeekly(1, 7, 30, 5, 19, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:29 Monday ~ 19:30 Wednesday every weekly
        window2 = RecurringTimeWindow.createWeekly(1, 7, 29, 3, 19, 30);
        Assert.isTrue(window1.conflictWith(window2), "conflicted expected");

        //07:30 Monday ~ 19:31 Friday every weekly
        window2 = RecurringTimeWindow.createWeekly(1, 7, 30, 5, 19, 31);
        Assert.isTrue(window1.conflictWith(window2), "conflicted expected");
    }
}
