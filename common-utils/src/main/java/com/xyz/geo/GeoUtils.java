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

    public static List<Point> link(List<Point> points) {
        LineLink lineLink = new LineLink();
        return lineLink.link(points);
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    /**
     * 根据散列点绘制最佳路线
     * 原理:
     * 1. 一个点扩散寻找最近点形成线段, 寻找下一个点根据首尾判断, 延伸线段最终形成路线
     * 2. 计算3次(随机点开始, 头部开始, 尾部开始)得出最近距离返回路线
     *
     * @param points 散列点
     * @return 路线
     */
    public static List<Point> direction(List<Point> points) {
        TreeMap<Double, List<Point>> resultMap = new TreeMap<>();
        List<Point> sortList = new ArrayList<>(points.size());
        calc(sortList, points);
        System.out.println(sortList.stream().map(Point::format).collect(Collectors.toList()));
        resultMap.put(getSumDistance(sortList), new ArrayList<>(sortList));
        points = new ArrayList<>(sortList);
        sortList.clear();
        calc(sortList, points);
        System.out.println(sortList.stream().map(Point::format).collect(Collectors.toList()));
        points = new ArrayList<>(sortList);
        resultMap.put(getSumDistance(sortList), new ArrayList<>(sortList));
        Collections.reverse(points);
        sortList.clear();
        calc(sortList, points);
        System.out.println(sortList.stream().map(Point::format).collect(Collectors.toList()));
        resultMap.put(getSumDistance(sortList), new ArrayList<>(sortList));
        return resultMap.firstEntry().getValue();
    }

    /**
     * 获取路线最大距离
     *
     * @param sortList 线段
     * @return 距离
     */
    private static Double getSumDistance(List<Point> sortList) {
        for (int i = 0; i < sortList.size() - 1; i++) {
            sortList.get(i).setDistance(GeoUtils.distance(sortList.get(i), sortList.get(i + 1)));
        }
        return sortList.stream().mapToDouble(m -> m.getDistance() != null ? m.getDistance() : 0D).sum();
    }

    /**
     * 递归延伸
     */
    private static void calc(List<Point> sortList, List<Point> allPoints) {
        if (CollectionUtils.isEmpty(sortList)) {
            int index = 0;
            sortList.add(allPoints.get(index));
            allPoints.remove(allPoints.get(index));
        }
        calcNear(sortList, allPoints);
        if (CollectionUtils.isNotEmpty(allPoints)) {
            calc(sortList, allPoints);
        }
    }

    /**
     * 计算最近点并延伸
     */
    private static void calcNear(List<Point> sortList, List<Point> allPoints) {
        Point first = sortList.get(0);
        Point last = sortList.get(sortList.size() - 1);
        allPoints.forEach(next -> {
            setNear(first, next);
            if (last != first) {
                setNear(last, next);
            }
        });
        Point near;
        if (first.getDistance() < last.getDistance()) {
            sortList.add(0, first.getNear());
            near = first.getNear();
        } else {
            sortList.add(last.getNear());
            near = last.getNear();
        }
        allPoints.remove(near);
        clear(first);
        clear(last);
    }

    private static void clear(Point point) {
        point.setDistance(null);
        point.setNear(null);
    }

    /**
     * 寻找最近点
     */
    private static void setNear(Point first, Point next) {
        double distance = GeoUtils.distance(first, next);
        if (first.getNear() == null || distance < first.getDistance()) {
            first.setNear(next);
            first.setDistance(distance);
        }
    }
}
