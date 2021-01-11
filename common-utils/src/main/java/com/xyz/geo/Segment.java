package com.xyz.geo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Segment {
    private Point start;
    private Point end;

    public double length() {
        return start.distance(end);
    }

    public Segment reverse() {
        return new Segment(this.end, this.start);
    }

    @Override
    public String toString() {
        return "Segment{" +
                "start=" + start +
                ", end=" + end +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Segment segment = (Segment) o;
        return Objects.equals(start, segment.start) &&
                Objects.equals(end, segment.end);
    }

    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}
