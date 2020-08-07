package com.xyz.utils;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapUtilsTest {
    @Test
    public void testTransform() {
        Map<String, String> map = initMap();
        Map<String, String> convertMap = MapUtils.transform(map, x -> x.toLowerCase().replaceAll("-", ""));
        Assert.isTrue(convertMap.size() == 3, "size is not 3");
        Assert.isTrue(convertMap.containsKey("test1"), "test1 not found");
        Assert.isTrue(!convertMap.containsKey("Test1"), "Test1 found");
        Assert.isTrue("test1_value".equals(convertMap.get("test1")), "not test1_value");

        convertMap = MapUtils.transform(map,
                x -> x.toLowerCase().replaceAll("-", ""),
                y -> y.toUpperCase().replaceAll("-", ""));

        Assert.isTrue(convertMap.size() == 3, "size is not 3");
        Assert.isTrue(convertMap.containsKey("test1"), "test1 not found");
        Assert.isTrue(!convertMap.containsKey("Test1"), "Test1 found");
        Assert.isTrue("test1_value".toUpperCase().equals(convertMap.get("test1")), "not test1_value.toUpperCase()");

        Map<String, Integer> convertMap1 = MapUtils.transform(map,
                x -> x.toLowerCase().replaceAll("-", ""),
                String::length);
        Assert.isTrue(convertMap.size() == 3, "size is not 3");
        Assert.isTrue(convertMap.containsKey("test1"), "test1 not found");
        Assert.isTrue(!convertMap.containsKey("Test1"), "Test1 found");
        Assert.isTrue("test1_value".length() == convertMap1.get("test1"), "not test1_value.length()");
    }

    @Test
    public void testSort() {
        Map<String, String> map = initMap();
        List<Map.Entry<String, String>> sortedMap = MapUtils.sort(map, Comparator.comparing(x -> x.getKey().toString()));
        Assert.isTrue("Test1".equals(sortedMap.get(0).getKey()), "sort by key not correct");

        sortedMap = MapUtils.sort(map, (x, y) -> y.getKey().toString().compareTo(x.getKey().toString()));
        Assert.isTrue("test-2".equals(sortedMap.get(0).getKey()), "sort by key not correct");

        sortedMap = MapUtils.sort(map, Comparator.comparing(x -> x.getValue().toString()));
        Assert.isTrue("test1_value".equals(sortedMap.get(0).getValue()), "sort by value not correct");
    }

    @Test
    public void testSortAndJoin() {
        Map<String, String> map = initMap();
        String joinedString = MapUtils.sortAndJoin(map, Comparator.comparing(x -> x.getKey().toString()), "=", "&");
        Assert.isTrue(joinedString.startsWith("Test1="), "sort and join not correct");
    }

    private Map<String, String> initMap() {
        Map<String, String> map = Maps.newHashMap();
        map.put("Test1", "test1_value");
        map.put("test-2", "test2_value");
        map.put("Test3", "test3_value");
        map.put("test-1", "test2_value");
        return map;
    }
}
