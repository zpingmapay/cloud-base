package com.xyz.utils;

import com.google.common.collect.ImmutableSortedMap;

import java.util.*;
import java.util.stream.Collectors;

public class MapUtils {
    public static <K, V, NK> Map<NK, V> transform(Map<K, V> map, Transformer<? super K, NK> keyFunc) {
        ValidationUtils.notNull(keyFunc, "Key function is required");
        return map.keySet().stream().collect(
                Collectors.toMap(
                        keyFunc::transform,
                        map::get,
                        (first, second) -> first,
                        HashMap::new));
    }

    public static <K, V, NK, NV> Map<NK, NV> transform(Map<K, V> map, Transformer<? super K, NK> keyFunc, Transformer<? super V, NV> valueFunc) {
        ValidationUtils.notNull(keyFunc, "Key function is required");
        ValidationUtils.notNull(valueFunc, "Value function is required");
        return map.keySet().stream().collect(
                Collectors.toMap(
                        keyFunc::transform,
                        eachKey -> valueFunc.transform(map.get(eachKey)),
                        (first, second) -> first,
                        HashMap::new));
    }

    public static <K, V> List<Map.Entry<K, V>> sort(Map<K, V> map, Comparator<? super Map.Entry<? super K, ? super V>> comparator) {
        return map.entrySet().stream().sorted(comparator).collect(Collectors.toList());
    }

    @FunctionalInterface
    public interface Transformer<F, T> {
        T transform(F f);
    }
}
