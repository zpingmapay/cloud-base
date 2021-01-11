package com.xyz.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Point {
    private double lat;
    private double lon;

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
        return String.format("%s,%s", this.lat, this.lon);
    }

}
