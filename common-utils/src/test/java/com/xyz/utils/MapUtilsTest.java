package com.xyz.utils;

import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Map;

public class MapUtilsTest {
    @Test
    public void should_all_success() {
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
                    y -> y.length());
        Assert.isTrue(convertMap.size() == 3, "size is not 3");
        Assert.isTrue(convertMap.containsKey("test1"), "test1 not found");
        Assert.isTrue(!convertMap.containsKey("Test1"), "Test1 found");
        Assert.isTrue("test1_value".length() == convertMap1.get("test1"), "not test1_value.length()");

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
