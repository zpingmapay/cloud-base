package com.xyz.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class LocalCache<K, V> implements ICache<K, V> {
    private final Cache<K, V> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build();

    @Override
    public void put(K key, V value) {
        this.put(key, value, 30, TimeUnit.DAYS);
    }

    @Override
    public void putIfAbsent(K key, V value) {
        this.putIfAbsent(key, value, 30, TimeUnit.DAYS);
    }

    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        this.cache.put(key, value);
    }

    @Override
    public void putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit) {
        if (!this.containsKey(key)) {
            this.cache.put(key, value);
        }
    }

    @Override
    public V get(K key) {
        return this.cache.getIfPresent(key);
    }

    @Override
    public void remove(K key) {
        this.cache.invalidate(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.cache.getIfPresent(key) != null;
    }

    @Override
    public Collection<V> values() {
        return this.cache.asMap().values();
    }

    @Override
    public long size() {
        return this.cache.size();
    }
}
