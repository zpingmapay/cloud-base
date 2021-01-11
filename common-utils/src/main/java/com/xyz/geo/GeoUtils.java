package com.xyz.geo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GeoUtils {
    private static final double EARTH_RADIUS = 6370.996;//赤道半径

    public static String geoHash(double lat, double lon, int precision) {
        return GeoHash.withCharacterPrecision(lat,lon, precision).toBase32();
    }

    public static List<String> neighborsGeoHash(double lat, double lon, int precision) {
        GeoHash[] neighbors = GeoHash.withCharacterPrecision(lat, lon, precision).getAdjacent();
        return Arrays.stream(neighbors).map(GeoHash::toBase32).collect(Collectors.toList());
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

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
