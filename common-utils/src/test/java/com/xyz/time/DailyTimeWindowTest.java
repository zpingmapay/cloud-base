package com.xyz.time;


import com.xyz.timewindow.RecurringTimeWindow;
import com.xyz.utils.TimeUtils;
import com.xyz.utils.ValidationUtilsTest;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DailyTimeWindowTest {
    @Test
    public void testIsInvalid() {
        RecurringTimeWindow window = RecurringTimeWindow.createDaily(7, 0, 8, 0);
        Assert.isTrue(window.isValid(), "not valid");

        window = RecurringTimeWindow.createDaily(23, 0, 1, 0);
        Assert.isTrue(window.isValid(), "not valid");

        ValidationUtilsTest.assertException((x) -> {
            RecurringTimeWindow window1 = RecurringTimeWindow.createDaily(7, 0, 7, 0);
            Assert.isTrue(!window1.isValid(), "not valid");
        }, IllegalArgumentException.class);
    }

    @Test
    public void testIsInTimeWindow1() {
        //7:00 ~ 19:00 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily("07:00", "19:00");

        Date date = TimeUtils.parse("2019-03-19 19:00:00", TimeUtils.DATE_FULL_STR);
        boolean inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "19:00 is not in span");

        date = TimeUtils.parse("2019-03-19 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan,"7:00 is in span");

        date = TimeUtils.parse("2019-03-19 23:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue( !inTimeSpan, "23:00 is not in span");

        date = TimeUtils.parse("2019-03-19 00:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue( !inTimeSpan, "00:00 is not in span");

        date = TimeUtils.parse("2019-03-19 12:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "12:00 is in span");

        date = TimeUtils.parse("2019-03-19 16:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue( inTimeSpan, "16:00 is in span");
    }


    @Test
    public void testIsInTimeWindow2() {
        //19:00 ~ 7:00 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily("19:00", "07:00");

        Date date = TimeUtils.parse("2019-03-19 19:00:00", TimeUtils.DATE_FULL_STR);
        boolean inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "19:00 is in span");

        date = TimeUtils.parse("2019-03-19 07:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "7:00 is not in span");

        date = TimeUtils.parse("2019-03-19 23:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "23:00 is in span");

        date = TimeUtils.parse("2019-03-19 00:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(inTimeSpan, "00:00 is in span");

        date = TimeUtils.parse("2019-03-19 12:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "12:00 is not in span");

        date = TimeUtils.parse("2019-03-19 16:00:00", TimeUtils.DATE_FULL_STR);
        inTimeSpan = window.isInTimeWindow(date.getTime());
        Assert.isTrue(!inTimeSpan, "16:00 is not in span");
    }

    @Test
    public void testGetNextStartTime1() {
        //7:00 ~ 19:00 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily(7, 0, 19, 0);

        Date date = TimeUtils.parse("2019-03-19 06:00:00", TimeUtils.DATE_FULL_STR);
        String startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-19 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-19 07:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 07:00:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-19 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 07:00:00".equals(startTime),"not the same");

        date = TimeUtils.parse("2019-02-28 20:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-01 07:00:00".equals(startTime), "not the same");
    }

    @Test
    public void testGetNextStartTime2() {
        //19:30 ~ 07:30 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily(19, 30, 7, 30);

        Date date = TimeUtils.parse("2019-03-19 19:00:00", TimeUtils.DATE_FULL_STR);
        String startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-19 19:30:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-19 19:30:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 19:30:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-03-19 20:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 19:30:00".equals(startTime), "not the same");

        date = TimeUtils.parse("2019-02-28 08:00:00", TimeUtils.DATE_FULL_STR);
        startTime = TimeUtils.format(new Date(window.getNextStartTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-02-28 19:30:00".equals(startTime), "not the same");
    }


    @Test
    public void testGetNextEndTime1() {
        //7:00 ~ 19:00 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily(7, 0, 19, 0);

        Date date = TimeUtils.parse("2019-03-19 18:00:00", TimeUtils.DATE_FULL_STR);
        String endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-19 19:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-19 19:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 19:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-19 20:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 19:00:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-02-28 20:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-01 19:00:00".equals(endTime), "not the same");
    }

    @Test
    public void testGetNextEndTime2() {
        //19:30 ~ 07:30 every day
        RecurringTimeWindow window = RecurringTimeWindow.createDaily(19, 30, 7, 30);

        Date date = TimeUtils.parse("2019-03-19 07:29:59", TimeUtils.DATE_FULL_STR);
        String endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-19 07:30:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-19 07:30:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 07:30:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-03-19 08:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-20 07:30:00".equals(endTime), "not the same");

        date = TimeUtils.parse("2019-02-28 16:00:00", TimeUtils.DATE_FULL_STR);
        endTime = TimeUtils.format(new Date(window.getNextEndTime(date.getTime())), TimeUtils.DATE_FULL_STR);
        Assert.isTrue("2019-03-01 07:30:00".equals(endTime), "not the same");
    }

    @Test
    public void testConflict1() {
        //19:30 ~ 07:30 every day
        RecurringTimeWindow window1 = RecurringTimeWindow.createDaily(19, 30, 7, 30);

        //07:30 ~ 08:30 every day
        RecurringTimeWindow window2 = RecurringTimeWindow.createDaily(7, 30, 8, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:30 ~ 19:30 every day
        window2 = RecurringTimeWindow.createDaily(7, 30, 19, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:29 ~ 19:30 every day
        window2 = RecurringTimeWindow.createDaily(7, 29, 19, 30);
        Assert.isTrue(window1.conflictWith(window2), "conflicted expected");

        //07:30 ~ 19:31 every day
        window2 = RecurringTimeWindow.createDaily(7, 30, 19, 31);
        Assert.isTrue(window1.conflictWith(window2), "conflicted expected");
    }

    @Test
    public void testConflict2() {
        //19:30 ~ 07:30 every day
        RecurringTimeWindow window1 = RecurringTimeWindow.createDaily("19:30", "07:30");

        //07:30 Monday ~ 08:30 Wednesday every weekly
        RecurringTimeWindow window2 = RecurringTimeWindow.createWeekly(1, "07:30", 1,"08:30");
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:30 Monday ~ 19:30 Wednesday every weekly
        window2 = RecurringTimeWindow.createWeekly(1, 7, 30, 3, 19, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:29 Monday ~ 19:30 Wednesday every weekly
        window2 = RecurringTimeWindow.createWeekly(4, 7, 29, 5, 19, 30);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");

        //07:30 Monday ~ 19:31 Wednesday every weekly
        window2 = RecurringTimeWindow.createWeekly(7, 7, 30, 1, 19, 31);
        Assert.isTrue(!window1.conflictWith(window2), "not conflicted expected");
    }

    @Test
    public void testConflict3() {
        //19:30 ~ 07:30 every day
        List<RecurringTimeWindow> list = new ArrayList<>();
        list.add(RecurringTimeWindow.createDaily("00:30", "07:30"));
        list.add(RecurringTimeWindow.createDaily("07:30", "08:30"));
        list.add(RecurringTimeWindow.createDaily("08:30", "09:30"));
        list.add(RecurringTimeWindow.createDaily("09:30", "10:30"));

        Assert.isTrue(!RecurringTimeWindow.conflictWithList(list), "not conflicted expected");
        list.add(RecurringTimeWindow.createDaily("10:00", "11:00"));
        Assert.isTrue(RecurringTimeWindow.conflictWithList(list), "conflicted expected");
    }
}
