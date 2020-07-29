package com.xyz.utils;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class UuidTest {
    @Test
    public void testGenerate() {
        int size = 1000000;
        duplicationCheck(Uuid::generate, size);
    }

    @Test
    public void testShortUuid() {
        int size = 1000000;
        duplicationCheck(Uuid::shortUuid, size);
    }

    private void duplicationCheck(Supplier<String> supplier, int size) {
        Set<String> uuids = IntStream.range(0, size).mapToObj(x -> supplier.get()).collect(Collectors.toSet());
        Assert.isTrue(uuids.size() == size, "duplicated uuid found");
    }
}
