package com.xyz.geo;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

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
}
