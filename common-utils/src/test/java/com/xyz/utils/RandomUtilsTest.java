package com.xyz.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomUtilsTest {

    @Test
    public void testRandomByPriority() {
        PriorityRule[] list = new PriorityRule[3];
        list[0] = new PriorityRule(1, 1);
        list[1] = new PriorityRule(2, 2);
        list[2] = new PriorityRule(3, 10);

        Map<Integer, List<PriorityRule>> group = IntStream.range(0, 10000)
                .mapToObj(x -> RandomUtils.randomByPriority(list))
                .collect(Collectors.groupingBy(PriorityRule::getId));
        int count1 = group.get(1).size();
        int count2 = group.get(2).size();
        int count3 = group.get(3).size();

        Assert.isTrue(count1 > count2 && count2 > count3 && count3 > 0, "by priority incorrect");
    }

    @Test
    public void testRandomByPercentage() {
        WeightRule[] list = new WeightRule[3];
        list[0] = new WeightRule(1, 60);
        list[1] = new WeightRule(2, 30);
        list[2] = new WeightRule(3, 10);


        Map<Integer, List<WeightRule>> group = IntStream.range(0, 10000)
                .mapToObj(x -> RandomUtils.randomByWeight(list))
                .collect(Collectors.groupingBy(WeightRule::getId));
        int count1 = group.get(1).size();
        int count2 = group.get(2).size();
        int count3 = group.get(3).size();

        Assert.isTrue(count1 > count2 && count2 > count3, "by weight incorrect");
    }

    @Data
    @AllArgsConstructor
    public static class PriorityRule implements Randomable.ByPriority {
        private int id;
        private int priority;
    }

    @Data
    @AllArgsConstructor
    public static class WeightRule implements Randomable.ByWeight {
        private int id;
        private int weight;
    }
}
