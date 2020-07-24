package com.xyz.utils;

import java.util.HashMap;
import java.util.Map;
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

    @FunctionalInterface
    public interface Transformer<F,T> {
        T transform(F f);
    }
}
