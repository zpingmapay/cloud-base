package com.xyz.cache;

import com.google.common.collect.Maps;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.function.Supplier;

public interface CacheManager {
    Map<String, ICache> localRepo = Maps.newConcurrentMap();
    Map<String, ICache> redisRepo = Maps.newConcurrentMap();

    static <K, V> ICache<K, V> getLocalCache(String namespace) {
        @SuppressWarnings("unchecked")
        ICache<K, V> cache = localRepo.get(namespace);
        if (cache == null) {
            cache = new LocalCache<>();
            ICache previousCache = localRepo.putIfAbsent(namespace, cache);
            if (previousCache != null) {
                cache = previousCache;
            }
        }
        return cache;
    }

    static <K, V> ICache<K, V> getRedisCache(String namespace, RedissonClient redissonClient) {
        @SuppressWarnings("unchecked")
        ICache<K, V> cache = redisRepo.get(namespace);
        if (cache == null) {
            cache = new RedisCache<>(redissonClient, namespace);
            ICache previousCache = redisRepo.putIfAbsent(namespace, cache);
            if (previousCache != null) {
                cache = previousCache;
            }
        }
        return cache;
    }

    static  <T> T getFromLocalCacheOrCreate(String namespace, String key, Supplier<T> func) {
        ICache<String, T> cache = CacheManager.getLocalCache(namespace);
        T t = cache.get(key);
        if(t == null) {
            t = func.get();
            cache.putIfAbsent(key, t);
        }
        return t;
    }

    static  <T> T getFromRedisOrCreate(String namespace, String key, RedissonClient redissonClient, Supplier<T> func) {
        ICache<String, T> cache = CacheManager.getRedisCache(namespace, redissonClient);
        T t = cache.get(key);
        if(t == null) {
            t = func.get();
            cache.putIfAbsent(key, t);
        }
        return t;
    }
}
