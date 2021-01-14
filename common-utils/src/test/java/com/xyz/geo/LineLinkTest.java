package com.xyz.geo;

import com.google.common.collect.Lists;
import com.xyz.utils.JsonUtils;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;
import java.util.stream.Collectors;

public class LineLinkTest {
    @Test
    public void test_link_happy_case() {
        List<Point> points = preparePointList();
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() == 10, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(9)), "tail not p10");
    }

    @Test
    public void test_link_two_line_case() {
        List<Point> points = preparePointList();
        points.remove(5);
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() == 9, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(8)), "tail not p10");
    }

    @Test
    public void test_link_three_line_case() {
        List<Point> points = preparePointList();
        points.remove(3);
        points.remove(6);
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() == 8, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(7)), "tail not p10");
    }

    @Test
    public void test_link_one_breakable_ring_case() {
        List<Point> points = preparePointList();
        points.add(new Point(31.198637, 121.498007, "p11"));
        LineLink lineLink = new LineLink(points, 1500);
        Line line = lineLink.link();
        Assert.isTrue(line.getPoints().size() == 11, "Not all points linked");
        Assert.isTrue(line.head().equals(points.get(0)), "head not p1");
        Assert.isTrue(line.tail().equals(points.get(9)), "tail not p10");
    }

    private List<Point> preparePointList() {
        Point p1 = new Point(31.243448, 121.490239, "p1");
        Point p2 = new Point(31.233321, 121.491956, "p2");
        Point p3 = new Point(31.223853, 121.50144, "p3");
        Point p4 = new Point(31.216513, 121.505818, "p4");
        Point p5 = new Point(31.209356, 121.501354, "p5");
        Point p6 = new Point(31.201354, 121.489123, "p6");
        Point p7 = new Point(31.197206, 121.477493, "p7");
        Point p8 = new Point(31.190855, 121.463889, "p8");
        Point p9 = new Point(31.186082, 121.454062, "p9");
        Point p10 = new Point(31.178849, 121.44256, "p10");
        return Lists.newArrayList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10);
    }

    @Test
    public void direction() {
        System.out.println(GeoUtils.direction(getG15()).stream().map(Point::format).collect(Collectors.toList()));
    }

    @Test
    public void testG15() {
        LineLink lineLink = new LineLink(getG15(), 120000);
        Line line = lineLink.link();
        System.out.println(JsonUtils.beanToJson(line));
    }

    private List<Point> getG15() {
        String data = "[{\"lat\":23.312814,\"lon\":113.068774,\"name\":\"113.0687740000,23.3128140000\"},{\"lat\":41.00447,\"lon\":122.775287,\"name\":\"122.7752870000,41.0044700000\"},{\"lat\":27.268572,\"lon\":120.229768,\"name\":\"120.2297680000,27.2685720000\"},{\"lat\":26.762225,\"lon\":119.59167,\"name\":\"119.5916700000,26.7622250000\"},{\"lat\":30.69723,\"lon\":121.178707,\"name\":\"121.1787070000,30.6972300000\"},{\"lat\":29.618259,\"lon\":121.450423,\"name\":\"121.4504230000,29.6182590000\"},{\"lat\":27.50857,\"lon\":120.381154,\"name\":\"120.3811540000,27.5085700000\"},{\"lat\":28.290656,\"lon\":121.120246,\"name\":\"121.1202460000,28.2906560000\"},{\"lat\":24.115121,\"lon\":117.74544,\"name\":\"117.7454400000,24.1151210000\"},{\"lat\":30.730237,\"lon\":121.27016,\"name\":\"121.2701600000,30.7302370000\"},{\"lat\":30.01672,\"lon\":121.423093,\"name\":\"121.4230930000,30.0167200000\"},{\"lat\":23.691488,\"lon\":117.160996,\"name\":\"117.1609960000,23.6914880000\"},{\"lat\":24.566627,\"lon\":117.931802,\"name\":\"117.9318020000,24.5666270000\"},{\"lat\":33.916089,\"lon\":119.969194,\"name\":\"119.9691940000,33.9160890000\"},{\"lat\":33.108684,\"lon\":120.384546,\"name\":\"120.3845460000,33.1086840000\"},{\"lat\":21.904998,\"lon\":111.953407,\"name\":\"111.9534070000,21.9049980000\"},{\"lat\":23.03495,\"lon\":116.273741,\"name\":\"116.2737410000,23.0349500000\"},{\"lat\":22.95658,\"lon\":115.842479,\"name\":\"115.8424790000,22.9565800000\"},{\"lat\":36.564908,\"lon\":119.012493,\"name\":\"119.0124930000,36.5649080000\"},{\"lat\":22.849234,\"lon\":114.54026,\"name\":\"114.5402600000,22.8492340000\"},{\"lat\":22.716957,\"lon\":112.991625,\"name\":\"112.9916250000,22.7169570000\"},{\"lat\":21.690409,\"lon\":111.490737,\"name\":\"111.4907370000,21.6904090000\"},{\"lat\":21.628722,\"lon\":111.058182,\"name\":\"111.0581820000,21.6287220000\"},{\"lat\":32.293681,\"lon\":120.771041,\"name\":\"120.7710410000,32.2936810000\"},{\"lat\":34.167558,\"lon\":119.694052,\"name\":\"119.6940520000,34.1675580000\"},{\"lat\":24.3245,\"lon\":117.863177,\"name\":\"117.8631770000,24.3245000000\"},{\"lat\":23.927264,\"lon\":117.556894,\"name\":\"117.5568940000,23.9272640000\"},{\"lat\":25.866797,\"lon\":119.352314,\"name\":\"119.3523140000,25.8667970000\"},{\"lat\":40.211808,\"lon\":122.112671,\"name\":\"122.1126710000,40.2118080000\"},{\"lat\":30.325781,\"lon\":121.185813,\"name\":\"121.1858130000,30.3257810000\"},{\"lat\":24.743208,\"lon\":118.437371,\"name\":\"118.4373710000,24.7432080000\"},{\"lat\":34.902446,\"lon\":119.120059,\"name\":\"119.1200590000,34.9024460000\"},{\"lat\":34.350155,\"lon\":119.437119,\"name\":\"119.4371190000,34.3501550000\"},{\"lat\":31.868382,\"lon\":120.993386,\"name\":\"120.9933860000,31.8683820000\"},{\"lat\":35.359632,\"lon\":119.378772,\"name\":\"119.3787720000,35.3596320000\"},{\"lat\":26.969708,\"lon\":120.199592,\"name\":\"120.1995920000,26.9697080000\"},{\"lat\":37.278358,\"lon\":120.748581,\"name\":\"120.7485810000,37.2783580000\"},{\"lat\":24.978047,\"lon\":118.669271,\"name\":\"118.6692710000,24.9780470000\"},{\"lat\":22.967245,\"lon\":115.970388,\"name\":\"115.9703880000,22.9672450000\"},{\"lat\":22.955724,\"lon\":115.666449,\"name\":\"115.6664490000,22.9557240000\"},{\"lat\":22.688741,\"lon\":113.99699,\"name\":\"113.9969900000,22.6887410000\"},{\"lat\":21.440118,\"lon\":110.456046,\"name\":\"110.4560460000,21.4401180000\"},{\"lat\":22.800237,\"lon\":115.099988,\"name\":\"115.0999880000,22.8002370000\"},{\"lat\":21.061339,\"lon\":110.078153,\"name\":\"110.0781530000,21.0613390000\"},{\"lat\":35.862255,\"lon\":119.906878,\"name\":\"119.9068780000,35.8622550000\"},{\"lat\":26.841373,\"lon\":119.791549,\"name\":\"119.7915490000,26.8413730000\"},{\"lat\":25.655063,\"lon\":119.289578,\"name\":\"119.2895780000,25.6550630000\"},{\"lat\":25.294282,\"lon\":118.986135,\"name\":\"118.9861350000,25.2942820000\"},{\"lat\":29.381909,\"lon\":121.449465,\"name\":\"121.4494650000,29.3819090000\"},{\"lat\":31.604503,\"lon\":121.039492,\"name\":\"121.0394920000,31.6045030000\"},{\"lat\":37.505849,\"lon\":121.106717,\"name\":\"121.1067170000,37.5058490000\"},{\"lat\":36.283543,\"lon\":119.951568,\"name\":\"119.95156800,36.28354300\"},{\"lat\":33.553178,\"lon\":120.210045,\"name\":\"120.2100450000,33.5531780000\"},{\"lat\":25.134757,\"lon\":118.825544,\"name\":\"118.8255440000,25.1347570000\"},{\"lat\":31.405306,\"lon\":121.187383,\"name\":\"121.1873830000,31.4053060000\"},{\"lat\":22.069556,\"lon\":112.223364,\"name\":\"112.2233640000,22.0695560000\"},{\"lat\":23.820932,\"lon\":117.38711,\"name\":\"117.3871100000,23.8209320000\"},{\"lat\":39.783045,\"lon\":121.888058,\"name\":\"121.8880580000,39.7830450000\"},{\"lat\":32.005088,\"lon\":120.992055,\"name\":\"120.9920550000,32.0050880000\"},{\"lat\":32.704614,\"lon\":120.492411,\"name\":\"120.4924110000,32.7046140000\"},{\"lat\":30.594319,\"lon\":121.03047,\"name\":\"121.0304700000,30.5943190000\"},{\"lat\":23.60877,\"lon\":116.74254,\"name\":\"116.7425400000,23.6087700000\"},{\"lat\":34.634304,\"lon\":119.085693,\"name\":\"119.0856930000,34.6343040000\"},{\"lat\":30.957878,\"lon\":121.307511,\"name\":\"121.3075110000,30.9578780000\"},{\"lat\":26.443231,\"lon\":119.495065,\"name\":\"119.4950650000,26.4432310000\"},{\"lat\":36.788854,\"lon\":120.373867,\"name\":\"120.3738670000,36.7888540000\"},{\"lat\":29.611421,\"lon\":121.441851,\"name\":\"121.4418510000,29.6114210000\"},{\"lat\":24.68753,\"lon\":118.197802,\"name\":\"118.1978020000,24.6875300000\"},{\"lat\":23.043917,\"lon\":112.898348,\"name\":\"112.8983480000,23.0439170000\"},{\"lat\":26.243783,\"lon\":119.547435,\"name\":\"119.5474350000,26.2437830000\"}]";
        return JsonUtils.jsonToList(data, Point.class);
    }
}
