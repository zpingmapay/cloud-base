package com.xyz.geo;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用一条线连接一组点，使得最终连成的线的长度最短。
 * <p>
 * 定义：
 * 最大连接距离: 超过该距离的点认为彼此不相邻；
 * 点的度: 通过该点连接的不超过最大连接距离的线段数量
 * 端点: 度为1的点
 * 奇节点: 度为奇数的节点
 * 孤点: 度为0的点
 * 环: 一组首尾相连的点
 * 孤环: 和外部没有线段连接的环
 * <p>
 * 有解的前提：
 * 1. 满足欧拉路径： 奇节点个数为偶数个
 * 2. 至少有一个端点
 * 3. 没有孤点
 * 4. 没过孤环
 * 即：这组点可以局部形成环，但环和外部有线段连接，且总体相互背离
 * <p>
 * 断线处理：
 * 按最大连接距离计算，最终连成了两条或多条线，即两条线上的任意两点的距离都超过最大连接距离，这种情况称为断线
 * 断线按端点最短距离连接
 * <p>
 * 拆环处理：
 * 按最大连接距离计算，某个点和2个或更多个点相连，形成环。
 * 拆环的原则:
 * 1. 度为大于1的奇数的点优先拆
 * 2. 使得环外相邻线段和拆后的环内线段总长度最短
 */
@AllArgsConstructor
public class LineLink {
    //待连接的一组点
    private final List<Point> points;
    //最大连接距离，超过该距离的点认为彼此不相邻
    private final long maxAdjacentDistance;

    public Line link() {
        List<Segment> possibleSegments = prepareAllPossibleSegments(points, maxAdjacentDistance);
        Map<Point, Integer> pointDegreeMap = calcPointDegree(possibleSegments);

        //孤点，无法连接
        List<Point> orphanPoints = pointDegreeMap.keySet().stream().filter(x -> pointDegreeMap.get(x) == 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(orphanPoints)) {
            throw new IllegalArgumentException("Fail to link these points, orphan point");
        }
        //欧拉路径：奇数个奇节点无法连接
        long oddCount = pointDegreeMap.keySet().stream().filter(x -> pointDegreeMap.get(x) % 2 == 1).count();
        if (oddCount % 2 == 1) {
            throw new IllegalArgumentException("Fail to link these points, odd endpoints");
        }

        long endpointCount = pointDegreeMap.keySet().stream().filter(x -> pointDegreeMap.get(x) == 1).count();
        //无端点，无法连接
        if (endpointCount == 0) {
            throw new IllegalArgumentException("Fail to link these points, no endpoint");
        }

        List<Point> rings = findCyclicRings(possibleSegments, pointDegreeMap);
        //孤环，无法连接
        if (CollectionUtils.isNotEmpty(rings) && isOrphanRing(possibleSegments, rings)) {
            throw new IllegalArgumentException("Fail to link these points, orphan ring");
        }

        if (CollectionUtils.isNotEmpty(rings)) {
            //无法被拆解的环
            boolean isUnbreakableRings = breakRings(possibleSegments, pointDegreeMap, rings);
            if (isUnbreakableRings) {
                throw new IllegalArgumentException("Fail to link these points, unbreakable ring");
            }
        }

        List<Line> lines = happyLink(possibleSegments, pointDegreeMap);
        return linkDisconnected(lines);
    }

    private List<Line> happyLink(List<Segment> segments, Map<Point, Integer> pointDegreeMap) {
        List<Point> endpoints = pointDegreeMap.keySet().stream().filter(x -> pointDegreeMap.get(x) == 1).collect(Collectors.toList());
        //无端点，所有点形成环，无法happy link
        if (CollectionUtils.isEmpty(endpoints)) {
            return null;
        }

        List<Line> result = Lists.newArrayList();
        while (CollectionUtils.isNotEmpty(endpoints)) {
            Point head = endpoints.remove(0);
            Line line = greedyLink(segments, pointDegreeMap, head);
            result.add(line);

            Point tail = line.tail().equals(head) ? line.head() : line.tail();
            if (pointDegreeMap.get(tail) == 1) {
                endpoints.remove(tail);
            }
        }
        return result;
    }

    private List<Point> findCyclicRings(List<Segment> possibleSegments, Map<Point, Integer> pointDegreeMap) {
        Map<Point, Integer> degrees = Maps.newHashMap(pointDegreeMap);
        findCyclicRingsByReduceDegree(possibleSegments, degrees);
        return degrees.keySet().stream().filter(x -> degrees.get(x) > 1).collect(Collectors.toList());
    }

    private void findCyclicRingsByReduceDegree(List<Segment> possibleSegments, Map<Point, Integer> pointDegreeMap) {
        List<Point> points = pointDegreeMap.keySet().stream().filter(x -> pointDegreeMap.get(x) == 1).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(points)) {
            return;
        }
        points.forEach(x -> pointDegreeMap.computeIfPresent(x, (k, v) -> v - 1));

        List<Point> adjacentPoints = possibleSegments
                .stream()
                .filter(x -> (points.contains(x.getStart()) && !points.contains(x.getEnd()))
                        || (points.contains(x.getEnd()) && !points.contains(x.getStart())))
                .map(seg -> points.contains(seg.getStart()) ? seg.getEnd() : seg.getStart())
                .collect(Collectors.toList());
        adjacentPoints.forEach(x -> pointDegreeMap.computeIfPresent(x, (k, v) -> v - 1));
        findCyclicRingsByReduceDegree(possibleSegments, pointDegreeMap);
    }

    private boolean isOrphanRing(List<Segment> possibleSegments, List<Point> rings) {
        List<Point> jointPoints = findJointPoints(possibleSegments, rings);
        return CollectionUtils.isEmpty(jointPoints);
    }

    private boolean breakRings(List<Segment> possibleSegments, Map<Point, Integer> pointDegreeMap, List<Point> rings) {
        List<Point> jointPoints = findJointPoints(possibleSegments, rings);
        List<Segment> misAdjacentSegments = possibleSegments
                .stream()
                .filter(x -> jointPoints.contains(x.getStart()) && jointPoints.contains(x.getEnd()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(misAdjacentSegments)) {
            return false;
        }

        possibleSegments.removeAll(misAdjacentSegments);
        reduceDegreeAfterBreakRings(pointDegreeMap, misAdjacentSegments);
        List<Point> unbreakableRings = findCyclicRings(possibleSegments, pointDegreeMap);
        return !CollectionUtils.isEmpty(unbreakableRings);
    }

    private void reduceDegreeAfterBreakRings(Map<Point, Integer> pointDegreeMap, List<Segment> misAdjacentSegments) {
        misAdjacentSegments.forEach(x -> {
            pointDegreeMap.computeIfPresent(x.getStart(), (k, v) -> v-1);
            pointDegreeMap.computeIfPresent(x.getEnd(), (k, v) -> v-1);
        });
    }

    private List<Point> findJointPoints(List<Segment> possibleSegments, List<Point> rings) {
        return possibleSegments
                .stream()
                .filter(x -> (rings.contains(x.getStart()) && !rings.contains(x.getEnd())) ||
                        (rings.contains(x.getEnd()) && !rings.contains(x.getStart())))
                .map(x -> rings.contains(x.getStart()) ? x.getStart() : x.getEnd())
                .collect(Collectors.toList());
    }

    private Line greedyLink(List<Segment> segments, Map<Point, Integer> pointDegreeMap, Point start) {
        Line line = new Line();
        List<Point> linkedPoints = Lists.newArrayList();
        linkedPoints.add(start);

        Segment segment;
        Point endpoint = start;
        do {
            segment = findUnlinkedSegment(segments, endpoint, linkedPoints);
            if (segment == null) {
                break;
            }
            Point thatSide = segment.getStart().equals(endpoint) ? segment.getEnd() : segment.getStart();
            endpoint = thatSide;
            linkedPoints.add(thatSide);
            line.link(segment);

            if (pointDegreeMap.get(thatSide) > 2) {
                break;
            }
        } while (true);

        return line;
    }

    private Segment findUnlinkedSegment(List<Segment> segments, Point pt, List<Point> linkedPoint) {
        return segments.stream()
                .filter(x -> (pt.equals(x.getStart()) && !linkedPoint.contains(x.getEnd()))
                        || (pt.equals(x.getEnd()) && !linkedPoint.contains(x.getStart())))
                .findAny().orElse(null);
    }

    private Map<Point, Integer> calcPointDegree(List<Segment> segments) {
        Map<Point, Integer> pointDegreeMap = Maps.newHashMap();
        for (Point pt : this.points) {
            pointDegreeMap.put(pt, 0);
        }
        for (Segment segment : segments) {
            pointDegreeMap.put(segment.getStart(), pointDegreeMap.get(segment.getStart()) + 1);
            pointDegreeMap.put(segment.getEnd(), pointDegreeMap.get(segment.getEnd()) + 1);
        }
        return pointDegreeMap;
    }

    private List<Segment> prepareAllPossibleSegments(List<Point> points, long maxAdjacentDistance) {
        List<Segment> segments = Lists.newArrayList();
        for (int i = 0; i < points.size() - 1; i++) {
            for (int j = i + 1; j < points.size(); j++) {
                Point pi = points.get(i);
                Point pj = points.get(j);
                Segment segment = new Segment(pi, pj);
                if (segment.length() < maxAdjacentDistance) {
                    segments.add(segment);
                }
            }
        }
        return segments;
    }

    private Line linkDisconnected(List<Line> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            return null;
        }
        if (CollectionUtils.size(lines) == 1) {
            return lines.get(0);
        }

        List<Line> notLinkedLines = Lists.newArrayList(lines);
        Line line = notLinkedLines.remove(0);
        while (!notLinkedLines.isEmpty()) {
            Line toBeLink = notLinkedLines.stream()
                    .min(Comparator.comparing(x -> distance(line, x)))
                    .orElse(notLinkedLines.get(0));
            notLinkedLines.remove(toBeLink);
            link(line, toBeLink);
        }
        return line;
    }

    private double distance(Line l1, Line l2) {
        Segment segment = shortestSeg(l1, l2).getKey();
        return segment.length();
    }

    private void link(Line l1, Line l2) {
        Pair<Segment, LineLinkMode> shortestSegAndMode = shortestSeg(l1, l2);
        Segment segment = shortestSegAndMode.getKey();
        LineLinkMode mode = shortestSegAndMode.getValue();

        if (mode.firstReverse) {
            l1.reverse();
        }
        l1.link(segment);
        if (mode.secondReverse) {
            l2.reverse();
        }
        for (int i = 1; i < l2.getPoints().size(); i++) {
            l1.getPoints().addLast(l2.getPoints().get(i));
        }
    }

    private Pair<Segment, LineLinkMode> shortestSeg(Line l1, Line l2) {
        Map<Segment, LineLinkMode> segments = Maps.newHashMap();
        segments.put(new Segment(l1.head(), l2.head()), LineLinkMode.H2H);
        segments.put(new Segment(l1.head(), l2.tail()), LineLinkMode.H2T);
        segments.put(new Segment(l1.tail(), l2.head()), LineLinkMode.T2H);
        segments.put(new Segment(l1.tail(), l2.tail()), LineLinkMode.T2T);
        Segment segment = segments.keySet()
                .stream()
                .min(Comparator.comparing(Segment::length))
                .orElse(segments.keySet().iterator().next());
        return new ImmutablePair<>(segment, segments.get(segment));
    }

    @AllArgsConstructor
    public enum LineLinkMode {
        H2H(true, false), H2T(true, true), T2H(false, false), T2T(false, true);

        boolean firstReverse;
        boolean secondReverse;
    }
}
