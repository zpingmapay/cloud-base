package com.xyz.utils;

import com.google.common.collect.Lists;

import java.util.Comparator;
import java.util.List;

import static com.xyz.utils.Randomable.ByPriority.MIN_PRIORITY;

public class RandomUtils {
    public static <T extends Randomable.ByPriority> T randomByPriority(T[] list) {
        return randomByPriority(Lists.newArrayList(list));
    }

    public static <T extends Randomable.ByPriority> T randomByPriority(List<T> list) {
        ValidationUtils.notEmpty(list, "list can not be empty");
        int total = list.stream().mapToInt(Randomable.ByPriority::getPriority).sum();
        int max = list.stream().mapToInt(Randomable.ByPriority::getPriority).max().orElse(MIN_PRIORITY);

        int indexRandom = org.apache.commons.lang3.RandomUtils.nextInt(0, total);
        int index = 0;
        for (T t : list) {
            index += 1 + max - t.getPriority();
            if (indexRandom < index) {
                return t;
            }
        }
        return list.stream()
                .min(Comparator.comparing(Randomable.ByPriority::getPriority))
                .orElseThrow(IllegalStateException::new);
    }

    public static <T extends Randomable.ByWeight> T randomByWeight(T[] list) {
        return randomByWeight(Lists.newArrayList(list));
    }

    public static <T extends Randomable.ByWeight> T randomByWeight(List<T> list) {
        ValidationUtils.notEmpty(list, "list can not be empty");
        int total = list.stream().mapToInt(Randomable.ByWeight::getWeight).sum();
        ValidationUtils.isTrue(100 == total, "Total percentage is not 100%");
        int indexRandom = org.apache.commons.lang3.RandomUtils.nextInt(0, 100);
        int index = 0;
        for (T t : list) {
            index += t.getWeight();
            if (indexRandom < index) {
                return t;
            }
        }
        return list.stream()
                .max(Comparator.comparing(Randomable.ByWeight::getWeight))
                .orElseThrow(IllegalStateException::new);
    }
}
