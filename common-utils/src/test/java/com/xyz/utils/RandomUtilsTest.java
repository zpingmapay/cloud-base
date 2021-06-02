package com.xyz.utils;

import com.google.common.collect.Lists;
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
    public void testPrioritableRandom() {
        List<PriorityRule> list = Lists.newArrayList();
        list.add(new PriorityRule(1, 1));
        list.add(new PriorityRule(2, 2));
        list.add(new PriorityRule(3, 3));


        Map<Integer, List<PriorityRule>> group = IntStream.range(0, 10000)
                .mapToObj(x -> RandomUtils.randomByPriority(list))
                .collect(Collectors.groupingBy(PriorityRule::getId));
        int count1 = group.get(1).size();
        int count2 = group.get(2).size();
        int count3 = group.get(3).size();

        Assert.isTrue(count1> count2 && count2 > count3, "priority incorrect");
    }

    @Test
    public void testPertentagableRandom() {
        PercentageRule[] list = new PercentageRule[3];
        list[0] = new PercentageRule(1, 60);
        list[1] = new PercentageRule(2, 30);
        list[2] = new PercentageRule(3, 10);


        Map<Integer, List<PercentageRule>> group = IntStream.range(0, 10000)
                .mapToObj(x -> RandomUtils.randomByPercentage(list))
                .collect(Collectors.groupingBy(PercentageRule::getId));
        int count1 = group.get(1).size();
        int count2 = group.get(2).size();
        int count3 = group.get(3).size();

        Assert.isTrue(count1> count2 && count2 > count3, "percentage incorrect");
    }

    @Data
    @AllArgsConstructor
    public static class PriorityRule implements RandomUtils.Prioritiable {
        private int id;
        private int priority;
    }

    @Data
    @AllArgsConstructor
    public static class PercentageRule implements RandomUtils.Percentagable {
        private int id;
        private int percentage;
    }
}
