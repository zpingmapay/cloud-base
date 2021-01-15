package com.xyz.geo;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class LineLink {
    public List<Point> link(List<Point> points) {
        List<Node> nodes = setupNodes(points);

        Node node = nodes.remove(0);
        node.linked = true;
        while (CollectionUtils.isNotEmpty(nodes)) {
            Node unlinkedNeighbor = node.findUnlinkedNeighbor();
            if (unlinkedNeighbor != null) {
                nodes.remove(unlinkedNeighbor);
                node = linkNeighbor(node, unlinkedNeighbor);
            } else {
                Node breakPoint = findNewNeighbor(node, nodes);
                if (breakPoint != null && breakPoint.point.equals(node.point)) {
                    continue;
                }
                node = backTracking(nodes, node, breakPoint);
            }
        }
        return toPoints(node);
    }

    private List<Node> setupNodes(List<Point> points) {
        List<Node> nodes = points.stream().map(x -> {
                    Node node = new Node(x);
                    node.index = 0;
                    node.linked = false;
                    node.prev = null;
                    node.next = null;
                    return node;
                }
        ).collect(Collectors.toList());

        for (Node node : nodes) {
            node.setNeighbors(nodes, 3);
        }

        Comparator<Node> latComparator = Comparator.comparing(x -> x.point.getLat());
        Comparator<Node> lonComparator = Comparator.comparing(x -> x.point.getLon());
        nodes.sort(latComparator.thenComparing(lonComparator));

        return nodes;
    }

    private Node findNewNeighbor(Node tail, List<Node> points) {
        Node breakPoint = null;
        Node newNeighbor = null;
        double distance = Double.MAX_VALUE;
        Node node = tail;
        while (node != null) {
            Node neighbor = findUnlinkedNeighbor(node, points);
            double tempDistance = GeoUtils.distance(node.point, neighbor.point);
            if (tempDistance < distance) {
                breakPoint = node;
                newNeighbor = neighbor;
                distance = tempDistance;
            }
            node = node.prev;
        }
        if (newNeighbor != null) {
            breakPoint.neighbors.add(newNeighbor);
            return breakPoint;
        }
        return null;
    }

    private Node findUnlinkedNeighbor(Node node, List<Node> points) {
        Comparator<ImmutablePair<Node, Node>> comparator = Comparator.comparing(x -> GeoUtils.distance(x.left.point, x.right.point));
        return points.stream()
                .filter(x -> !x.point.equals(node.point) && !x.linked)
                .map(x -> new ImmutablePair<>(x, node))
                .sorted(comparator)
                .limit(1)
                .map(ImmutablePair::getLeft)
                .findAny()
                .orElse(null);
    }

    private Node linkNeighbor(Node node, Node unlinkedNeighbor) {
        unlinkedNeighbor.linked = true;
        node.next = unlinkedNeighbor;
        unlinkedNeighbor.prev = node;
        return unlinkedNeighbor;
    }

    private Node backTracking(List<Node> nodes, Node node, Node breakPoint) {
        if(breakPoint != null) {
            //back tracking to break point
            while (!node.point.equals(breakPoint.point)) {
                node.linked = false;
                nodes.add(node);
                node = node.prev;
                node.next = null;
            }
        }
        //back tracking
        node.linked = false;
        nodes.add(node);
        node = node.prev;
        node.next = null;
        node.index++;
        return node;
    }

    private List<Point> toPoints(Node node) {
        Node head = null;
        while (node.prev != null) {
            head = node.prev;
            node = head;
        }
        List<Point> list = Lists.newArrayList();
        if(head == null) {
            return list;
        }
        while (head.next != null) {
            list.add(head.point);
            head = head.next;
        }
        list.add(head.point);
        return list;
    }

    public static class Node {
        private final Point point;
        private Node prev;
        private Node next;
        private List<Node> neighbors;
        private boolean linked;
        private int index;

        public Node(Point point) {
            this.point = point;
        }

        public void setNeighbors(List<Node> points, int number) {
            Comparator<ImmutablePair<Node, Node>> comparator = Comparator.comparing(x -> GeoUtils.distance(x.left.point, x.right.point));
            this.neighbors = points.stream()
                    .filter(x -> !x.point.equals(this.point))
                    .map(x -> new ImmutablePair<>(x, this))
                    .sorted(comparator)
                    .limit(number)
                    .map(ImmutablePair::getLeft)
                    .collect(Collectors.toList());
        }

        public Node findUnlinkedNeighbor() {
            for (int i = this.index; i < this.neighbors.size(); i++) {
                Node next = this.neighbors.get(i);
                if (!next.linked) {
                    this.index = i + 1;
                    return next;
                }
            }
            return null;
        }
    }

}
