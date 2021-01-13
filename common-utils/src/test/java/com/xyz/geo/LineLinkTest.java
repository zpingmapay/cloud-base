package com.xyz.geo;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

public class LineLinkTest {
    @Test
    public void test_link_happy_case() {
        List<Point> points = preparePointList();
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() ==10, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(9)), "tail not p10");
    }

    @Test
    public void test_link_two_line_case() {
        List<Point> points = preparePointList();
        points.remove(5);
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() ==9, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(8)), "tail not p10");
    }

    @Test
    public void test_link_three_line_case() {
        List<Point> points = preparePointList();
        points.remove(3);
        points.remove(6);
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() ==8, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(7)), "tail not p10");
    }

    @Test
    public void test_link_one_breakable_ring_case() {
        List<Point> points = preparePointList();
        points.add(new Point(31.198637, 121.498007,"p11"));
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() ==11, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(9)), "tail not p10");
    }

    private List<Point> preparePointList() {
        Point p1 = new Point(31.243448, 121.490239, "p1");
        Point p2 = new Point(31.233321, 121.491956, "p2");
        Point p3 = new Point(31.223853, 121.50144, "p3");
        Point p4 = new Point(31.216513, 121.505818, "p4");
        Point p5 = new Point(31.209356, 121.501354, "p5");
        Point p6 = new Point(31.201354, 121.489123, "p6");
        Point p7 = new Point(31.197206, 121.477493, "p7");
        Point p8 = new Point(31.190855, 121.463889, "p8");
        Point p9 = new Point(31.186082, 121.454062, "p9");
        Point p10 = new Point(31.178849, 121.44256, "p10");
        return Lists.newArrayList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }
}
