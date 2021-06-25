package com.xyz.geo;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class SegmentTest {
    @Test
    public void testLength() {
        Point p1 = new Point(31.233321, 121.49217);
        Point p2 = new Point(31.239036, 121.490679);
        Segment segment = new Segment(p1, p2);
        Assert.isTrue(segment.length() > 600, "Len should > 600 meters");
    }

    @Test
    public void testReverse() {
        Point p1 = new Point(31.233321, 121.49217);
        Point p2 = new Point(31.239036, 121.490679);
        Segment segment = new Segment(p1, p2);

        Segment segment1 = segment.reverse();
        Assert.isTrue(segment.length() == segment1.length(), "Len should be the same");
        Assert.isTrue(!segment1.equals(segment), "segments not equal");
        Assert.isTrue(segment1.equals(new Segment(p2, p1)), "segments not equal");
    }
}
