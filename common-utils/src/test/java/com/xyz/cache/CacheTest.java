package com.xyz.cache;

import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

public class CacheTest {
    @Test
    public void should_all_success() {
        ICache<String, String> cache = CacheManager.getLocalCache("test");
        cache.put("1", "test1");
        cache.put("2", "test2");
        Assert.isTrue(cache.containsKey("1"), "1 is expected in the cache");
        String value = cache.get("1");
        Assert.isTrue("test1".equals(value), "test1 is expected");
        cache.remove("1");
        Assert.isTrue(!cache.containsKey("1"), "1 is removed from the cache");
    }

    @Test
    public void testGetOrCreate() {
        ICache<String, String> cache = CacheManager.getLocalCache("test");
        String value = cache.getOrCreate("1", (k) -> "test1");
        Assert.isTrue("test1".equals(value), "not test1");
        Assert.isTrue(!cache.putIfAbsent("1", "test2"), "key 1 already exist");
        Assert.isTrue("test1".equals(cache.getOrCreate("1", (k) -> "test2")), "key 1 already exist");
        Assert.isTrue("test2".equals(cache.getOrCreate("2", (k) -> "test2")), "key 2 should put success");
    }
}
