package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UuidTest {
    @Test
    public void testGenerate() {
        int size = 1000000;
        Set<String> uuids = IntStream.range(0, size).mapToObj(x -> Uuid.generate()).collect(Collectors.toSet());
        Assert.isTrue(uuids.size() == size, "duplicated uuid found");
    }

    @Test
    public void testShortUuid() {
        int size = 1000000;
        Set<String> uuids = IntStream.range(0, size).mapToObj(x -> Uuid.shortUuid()).collect(Collectors.toSet());
        Assert.isTrue(uuids.size() == size, "duplicated uuid found");
    }
}
