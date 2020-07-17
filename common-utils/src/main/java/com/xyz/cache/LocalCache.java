package com.xyz.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

public class LocalCache<K, V> implements ICache<K, V> {
    private final Cache<K, V> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.DAYS)
            .build();

    @Override
    public void put(K key, V value) {
        this.cache.put(key, value);
    }

    @Override
    public void put(K key, V value, long timeout, TimeUnit timeUnit) {
        this.cache.put(key, value);
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
}
