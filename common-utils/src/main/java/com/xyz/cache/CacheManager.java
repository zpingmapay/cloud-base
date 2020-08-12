package com.xyz.cache;

import com.google.common.collect.Maps;
import org.redisson.api.RedissonClient;

import java.util.Map;
import java.util.function.Function;

public interface CacheManager {
    Map<String, ICache> localRepo = Maps.newConcurrentMap();
    Map<String, ICache> redisRepo = Maps.newConcurrentMap();

    @SuppressWarnings("unchecked")
    static <K, V> ICache<K, V> getLocalCache(String namespace) {
        return localRepo.computeIfAbsent(namespace, c -> new LocalCache<>());
    }

    @SuppressWarnings("unchecked")
    static <K, V> ICache<K, V> getRedisCache(String namespace, RedissonClient redissonClient) {
        return redisRepo.computeIfAbsent(namespace, c -> new RedisCache<>(redissonClient, namespace));
    }

    static <T> T getFromLocalOrCreate(String namespace, String key, Function<String, ? extends T> func) {
        ICache<String, T> cache = CacheManager.getLocalCache(namespace);
        return  cache.getOrCreate(key, func);
    }

    static <T> T getFromRedisOrCreate(String namespace, String key, RedissonClient redissonClient, Function<String, ? extends T> func) {
        ICache<String, T> cache = CacheManager.getRedisCache(namespace, redissonClient);
        return  cache.getOrCreate(key, func);
    }
}
