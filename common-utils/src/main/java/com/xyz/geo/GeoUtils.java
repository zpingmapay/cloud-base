package com.xyz.geo;

import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GeoUtils {
    private static final double EARTH_RADIUS = 6370.996;//赤道半径

    public static String geoHash(double lat, double lon, int precision) {
        return GeoHash.withCharacterPrecision(lat, lon, precision).toBase32();
    }

    public static String geoHash(Point p, int precision) {
        return geoHash(p.getLat(), p.getLon(), precision);
    }

    public static List<String> neighborsGeoHash(double lat, double lon, int precision) {
        GeoHash[] neighbors = GeoHash.withCharacterPrecision(lat, lon, precision).getAdjacent();
        return Arrays.stream(neighbors).map(GeoHash::toBase32).collect(Collectors.toList());
    }

    public static List<String> neighborsGeoHash(Point p, int precision) {
        return neighborsGeoHash(p.getLat(), p.getLon(), precision);
    }

    public static double distance(double lat1, double lon1, double lat2, double lon2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double diffLat = radLat1 - radLat2;
        double diffLon = rad(lon1) - rad(lon2);
        double distance = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(diffLat / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(diffLon / 2), 2)));
        distance = distance * EARTH_RADIUS * 1000;
        BigDecimal decimal = new BigDecimal(distance);
        return decimal.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double distance(Point p1, Point p2) {
        return distance(p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
    }

    public static boolean isClockwise(Point p0, Point p1, Point p2) {
        double vector = p1.getLat() * p2.getLon() - p2.getLat() * p1.getLon() - (p2.getLon() - p1.getLon()) * p0.getLat() + (p2.getLat() - p1.getLat()) * p0.getLon();
        return vector < 0;
    }

    public static boolean isClockwise(List<Point> points) {
        if (CollectionUtils.isEmpty(points) || points.size() < 3) {
            throw new IllegalArgumentException("At least three points are required");
        }
        Point p0 = points.get(0);
        Point p1 = points.get(1);
        Point p2 = points.get(2);
        return isClockwise(p0, p1, p2);
    }

    public static int degree(double vertexPointX, double vertexPointY, double point1X, double point1Y, double point2X, double point2Y) {
        //向量的点乘
        double vector = (point1X - vertexPointX) * (point2X - vertexPointX) + (point1Y - vertexPointY) * (point2Y - vertexPointY);
        //向量的模乘
        double sqrt = Math.sqrt(
                (Math.abs((point1X - vertexPointX) * (point1X - vertexPointX)) + Math.abs((point1Y - vertexPointY) * (point1Y - vertexPointY)))
                        * (Math.abs((point2X - vertexPointX) * (point2X - vertexPointX)) + Math.abs((point2Y - vertexPointY) * (point2Y - vertexPointY)))
        );
        //反余弦计算弧度
        double radian = Math.acos(vector / sqrt);
        //弧度转角度制
        return (int) (180 * radian / Math.PI);
    }

    /**
     * 计算点p1，p2相对顶点vertex的角度
     *
     * @param vertex 顶点
     * @param p1     点p1
     * @param p2     点p2
     * @return 点p1，p2相对顶点vertex的角度
     */
    public static int degree(Point vertex, Point p1, Point p2) {
        return degree(vertex.getLat(), vertex.getLon(), p1.getLat(), p1.getLon(), p2.getLat(), p2.getLon());
    }

    public static Line link(List<Point> points) {
        LineLink lineLink = new LineLink();
        return lineLink.link(points);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
