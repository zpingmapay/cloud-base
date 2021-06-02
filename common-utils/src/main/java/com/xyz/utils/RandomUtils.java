package com.xyz.utils;

import java.util.Comparator;
import java.util.List;

import static com.xyz.utils.RandomUtils.Prioritiable.MIN_PRIORITY;

public class RandomUtils {
    public static <T extends Prioritiable> T randomByPriority(List<T> list) {
        ValidationUtils.notEmpty(list, "list can not be empty");
        int total = list.stream().mapToInt(Prioritiable::getPriority).sum();
        int max = list.stream().mapToInt(Prioritiable::getPriority).max().orElse(MIN_PRIORITY);

        int indexRandom = org.apache.commons.lang3.RandomUtils.nextInt(0, total);
        int index = 0;
        for (T t : list) {
            index += 1 + max - t.getPriority();
            if (indexRandom < index) {
                return t;
            }
        }
        return list.stream()
                .min(Comparator.comparing(Prioritiable::getPriority))
                .orElseThrow(IllegalStateException::new);
    }


    public static <T extends Percentagable> T randomByPercentage(List<T> list) {
        ValidationUtils.notEmpty(list, "list can not be empty");
        int total = list.stream().mapToInt(Percentagable::getPercentage).sum();
        ValidationUtils.isTrue(100 == total, "Total percentage is not 100%");
        int indexRandom = org.apache.commons.lang3.RandomUtils.nextInt(0, 100);
        int index = 0;
        for (T t : list) {
            index += t.getPercentage();
            if (indexRandom < index) {
                return t;
            }
        }
        return list.stream()
                .max(Comparator.comparing(Percentagable::getPercentage))
                .orElseThrow(IllegalStateException::new);
    }


    public interface Prioritiable {
        int MAX_PRIORITY = 1;
        int MIN_PRIORITY = 10;

        int getPriority();
    }

    public interface Percentagable {
        int getPercentage();
    }
}
