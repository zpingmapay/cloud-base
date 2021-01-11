package com.xyz.geo;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class GeoUtilsTest {
    @Test
    public void testDistance() {
        Point p1 = new Point(31.233321, 121.49217);
        Point p2 = new Point(31.239036,121.490679);
        double distance = GeoUtils.distance(31.233321, 121.49217, 31.239036,121.490679);

        Assert.isTrue(distance - p2.distance(p1)<0.0000000001D, "distance not correct");
        Assert.isTrue(distance>600, "distance not correct");
    }

    @Test
    public void testGeoHash() {
        String geoHash = GeoUtils.geoHash(31.233321, 121.49217, 6);
        Assert.isTrue("wtw3st".equals(geoHash), "Geo hash not correct");
        String geoHash1 = GeoUtils.geoHash(31.239036,121.490679, 6);
        Assert.isTrue(!"wtw3st".equals(geoHash1), "Geo hash not correct");
    }

    @Test
    public void testNeighbors() {
        List<String> neighbors = GeoUtils.neighborsGeoHash(31.233321, 121.49217, 6);
        String geoHash = GeoUtils.geoHash(31.239036,121.490679, 6);
        Assert.isTrue(neighbors.contains(geoHash), "Neighbors not correct");
    }

    @Test
    public void testIsClockwise() {
        List<Point> points = new ArrayList<>();
        points.add(new Point(121.4756906033, 31.2303806967));
        points.add(new Point(121.4738023281, 31.2302063883));
        points.add(new Point(121.4746928215, 31.2298394224));
        Assert.isTrue(!GeoUtils.isClockwise(points), "should not be clockwise");

        points = new ArrayList<>();
        points.add(new Point(116.403322, 39.920255));
        points.add(new Point(116.410703, 39.897555));
        points.add(new Point(116.402292, 39.892353));
        points.add(new Point(116.389846, 39.891365));

        Assert.isTrue(GeoUtils.isClockwise(points), "should be clockwise");
    }

    @Test
    public void testDegree() {
        Point v = new Point(0, 0);
        Point p1 = new Point(1, 1);
        Point p2 = new Point(1, 0);
        int degree = GeoUtils.degree(v, p1, p2);
        Assert.isTrue(45 == degree, "degree should be 45");
        degree = GeoUtils.degree(v, p2, p1);
        Assert.isTrue(45 == degree, "degree should be 45");
    }
}
