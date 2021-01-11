package com.xyz.geo;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.LinkedList;

public class LineTest {
    @Test
    public void testLength() {
        Point p1 = new Point(31.239036, 121.490679);
        Point p2 = new Point(31.229722, 121.496076);
        Point p3 = new Point(31.222217, 121.503221);

        LinkedList<Point> points = new LinkedList<>();
        points.addLast(p1);
        points.addLast(p2);
        points.addLast(p3);
        Line line = new Line(points);

        Assert.isTrue(line.head().equals(p1), "head not p1");
        Assert.isTrue(line.tail().equals(p3), "tail not p3");

        double length = line.length();
        Assert.isTrue(length == GeoUtils.distance(p1, p2) + GeoUtils.distance(p2, p3), "len should > 1000 meters");
    }

    @Test
    public void testAdjacentTo() {
        Point p1 = new Point(31.239036, 121.490679);
        Point p2 = new Point(31.229722, 121.496076);
        Point p3 = new Point(31.222217, 121.503221);
        LinkedList<Point> points = new LinkedList<>();
        points.addLast(p1);
        points.addLast(p2);
        points.addLast(p3);
        Line line = new Line(points);

        Point p4 = new Point(31.209041, 121.504359);
        Point p5 = new Point(31.219041, 122.504359);

        Assert.isTrue(line.adjacentTo(new Segment(p3, p4)), "should adjacent");
        Assert.isTrue(!line.adjacentTo(new Segment(p2, p3)), "should not adjacent");
        Assert.isTrue(!line.adjacentTo(new Segment(p4, p5)), "should not adjacent");
    }

    @Test
    public void testLink() {
        Point p1 = new Point(31.239036, 121.490679);
        Point p2 = new Point(31.229722, 121.496076);
        Point p3 = new Point(31.222217, 121.503221);
        LinkedList<Point> points = new LinkedList<>();
        points.addLast(p1);
        points.addLast(p2);
        points.addLast(p3);
        Line line = new Line(points);

        Point p4 = new Point(31.209041, 121.504359);
        Point p5 = new Point(31.219041, 122.504359);
        line.link(new Segment(p4, p3));
        Assert.isTrue(line.tail().equals(p4), "tail not p4");
        line.link(new Segment(p1, p5));
        Assert.isTrue(line.head().equals(p5), "head not p5");
    }

}
