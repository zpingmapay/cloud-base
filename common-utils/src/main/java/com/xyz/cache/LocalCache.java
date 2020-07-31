package com.xyz.cache;

import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class LocalCache<K, V> implements ICache<K, V> {
    private final Map<K, V> cache = Maps.newConcurrentMap();

    @Override
    public void put(K key, V value) {
        this.put(key, value, 30, TimeUnit.DAYS);
    }

    @Override
    public boolean putIfAbsent(K key, V value) {
        return this.putIfAbsent(key, value, 30, TimeUnit.DAYS);
    }

    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        this.cache.put(key, value);
    }

    @Override
    public boolean putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit) {
        return this.cache.putIfAbsent(key, value) == null;
    }

    @Override
    public V get(K key) {
        return this.cache.get(key);
    }

    @Override
    public V getOrCreate(K key, Function<? super K, ? extends V> func) {
        return this.cache.computeIfAbsent(key, func);
    }

    @Override
    public void remove(K key) {
        this.cache.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.containsKey(key);
    }

    @Override
    public Collection<V> values() {
        return this.cache.values();
    }

    @Override
    public long size() {
        return this.cache.size();
    }
}
