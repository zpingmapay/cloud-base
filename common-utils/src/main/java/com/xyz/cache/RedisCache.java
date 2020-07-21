package com.xyz.cache;

import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import java.util.Collection;
import java.util.concurrent.TimeUnit;

public class RedisCache<K, V> implements ICache<K, V> {
    private final RMapCache<K, V> rMapCache;

    public RedisCache(RedissonClient redissonClient, String namespace) {
        this.rMapCache = redissonClient.getMapCache(namespace, StringCodec.INSTANCE);
    }

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
        this.rMapCache.put(key, value, timeout, timeUnit);
    }

    @Override
    public void putIfAbsent(K key, V value, long timeout, TimeUnit timeUnit) {
        this.rMapCache.putIfAbsent(key, value, timeout, timeUnit);
    }

    @Override
    public V get(K key) {
        return this.rMapCache.get(key);
    }

    @Override
    public void remove(K key) {
        this.rMapCache.remove(key);
    }

    @Override
    public boolean containsKey(K key) {
        return this.rMapCache.containsKey(key);
    }

    @Override
    public Collection<V> values() {
        return this.rMapCache.values();
    }

    @Override
    public long size() {
        return this.rMapCache.size();
    }
}
