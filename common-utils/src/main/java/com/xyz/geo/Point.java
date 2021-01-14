package com.xyz.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Point {
    private double lat;
    private double lon;
    private String name;
    private Double distance;
    private Point near;

    public Point(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public Point(double lat, double lon, String name) {
        this.lat = lat;
        this.lon = lon;
        this.name = name;
    }

    /**
     * Calculate distance between two points
     *
     * @param that another point
     * @return distance in meters
     */
    public double distance(Point that) {
        return GeoUtils.distance(this, that);
    }

    /**
     * Get geo hash of the point
     *
     * @param precision geo hash length
     *                  <p>
     *                  length  -  width  -  height
     *                  3       - 156.5km -  156km
     *                  4       - 39.1km  -  19.5km
     *                  5       - 4.9km   -  4.9km
     *                  6       - 0.61km  - 0.61km
     * @return geo hash value
     */
    public String geoHash(int precision) {
        return GeoUtils.geoHash(this, precision);
    }

    /**
     * Get neighbors of the point
     *
     * @param precision geo hash length
     *                  <p>
     *                  length  -  width  -  height
     *                  3       - 156.5km -  156km
     *                  4       - 39.1km  -  19.5km
     *                  5       - 4.9km   -  4.9km
     *                  6       - 0.61km  - 0.61km
     * @return neighbors's geo hash
     */
    public List<String> neighborsGeoHash(int precision) {
        return GeoUtils.neighborsGeoHash(this, precision);
    }

    @Override
    public String toString() {
        return String.format("%s(%s,%s)", this.name, this.lat, this.lon);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return Double.compare(point.lat, lat) == 0 &&
                Double.compare(point.lon, lon) == 0;
    }

    public String format() {
        return "[" + this.getLon() + "," + getLat() + "]";
    }

    @Override
    public int hashCode() {
        return Objects.hash(lat, lon);
    }
}
