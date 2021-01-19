package com.xyz.geo;

import com.google.common.collect.Lists;
import com.xyz.utils.ValidationUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@AllArgsConstructor
@Data
public class Line {
    private LinkedList<Point> points;

    public Line() {
        this.points = Lists.newLinkedList();
    }

    public Line(List<Point> pts) {
        this.points = Lists.newLinkedList();
        for(Point pt : pts) {
            points.addLast(pt);
        }
    }

    public double length() {
        ValidationUtils.isTrue(CollectionUtils.size(points) > 1, "Line is empty");

        return this.toSegments()
                .stream()
                .map(x -> BigDecimal.valueOf(x.length()))
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO)
                .doubleValue();
    }

    public Point head() {
        ValidationUtils.isTrue(CollectionUtils.size(points) > 1, "Line is empty");

        return points.getFirst();
    }

    public Point tail() {
        ValidationUtils.isTrue(CollectionUtils.size(points) > 1, "Line is empty");

        return points.getLast();
    }

    public void link(Segment segment) {
        if(CollectionUtils.isEmpty(this.points)) {
            this.points.addFirst(segment.getStart());
            this.points.addLast(segment.getEnd());
        } else {
            ValidationUtils.isTrue(adjacentTo(segment), "Can not link segment which is not adjacent to this line");
            if (segment.getStart().equals(this.head())) {
                this.points.addFirst(segment.getEnd());
            } else if (segment.getEnd().equals(this.head())) {
                this.points.addFirst(segment.getStart());
            } else if (segment.getStart().equals(this.tail())) {
                this.points.addLast(segment.getEnd());
            } else if (segment.getEnd().equals(this.tail())) {
                this.points.addLast(segment.getStart());
            }
        }
    }

    public boolean adjacentTo(Segment segment) {
        if (CollectionUtils.isEmpty(this.points)) {
            return true;
        }
        boolean adjacentTo = this.head().equals(segment.getStart())
                || this.head().equals(segment.getEnd())
                || this.tail().equals(segment.getStart())
                || this.tail().equals(segment.getEnd());

        return adjacentTo && !this.points.containsAll(segment.toPoints());
    }

    public List<Segment> toSegments() {
        ValidationUtils.isTrue(CollectionUtils.size(points) > 1, "Line is empty");

        List<Segment> segments = new ArrayList<>();
        Point start = this.head();
        Point end = points.get(1);
        segments.add(new Segment(start, end));
        for (Iterator<Point> it = this.points.listIterator(1); it.hasNext(); ) {
            start = end;
            end = it.next();
            segments.add(new Segment(start, end));
        }
        return segments;
    }

    public void reverse() {
        LinkedList<Point> points = Lists.newLinkedList();
        for (Point pt : this.points) {
            points.addFirst(pt);
        }
        this.points = points;
    }

    @Override
    public String toString() {
        return "Line{" +
                "points=" + points +
                '}';
    }
}
