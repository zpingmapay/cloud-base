package com.xyz.cache;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public interface ICache<K,V> {
    void put(K key, V value);

    void put(K key, V value, long timeout, TimeUnit timeUnit);

    V get(K key);

    void remove(K key);

    boolean containsKey(K key);

    Collection<V> values();

    long size();
}
