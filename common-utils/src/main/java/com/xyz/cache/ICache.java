package com.xyz.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ICache<K,V> {
    void put(K key, V value);

    boolean putIfAbsent(K key, V value);

    void put(K key, V value, long timeout, TimeUnit timeUnit);

    boolean putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit);

    V get(K key);

    V getOrCreate(K key, Function<? super K, ? extends V> func);

    void remove(K key);

    boolean containsKey(K key);

    Collection<V> values();

    long size();
}
