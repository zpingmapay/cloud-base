package com.xyz.geo;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;

public class PointTest {
    @Test
    public void testDistance() {
        Point p1 = new Point(31.233321, 121.49217);
        Point p2 = new Point(31.239036,121.490679);
        double distance = p1.distance(p2);
        Assert.isTrue(distance - p2.distance(p1)<0.0000000001D, "distance not correct");
        Assert.isTrue(distance>600, "distance not correct");
    }

    @Test
    public void testGeoHash() {
        Point p = new Point(31.233321, 121.49217);
        String geoHash = p.geoHash(6);
        Assert.isTrue("wtw3st".equals(geoHash), "Geo hash not correct");
        p = new Point(31.239036,121.490679);
        Assert.isTrue(!"wtw3st".equals(p.geoHash(6)), "Geo hash not correct");
    }

    @Test
    public void testNeighbors() {
        Point p = new Point(31.233321, 121.49217);
        List<String> neighbors = p.neighborsGeoHash(6);
        p = new Point(31.239036,121.490679);
        Assert.isTrue(neighbors.contains(p.geoHash(6)), "Neighbors not correct");
    }
}
