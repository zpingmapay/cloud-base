package com.xyz.cloud.retry.sotre;

import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.StringCodec;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class RedisEventStore implements EventStore {
    private static final int TTL_IN_HOURS = 24;
    private static final String CACHE_PREFIX = "com.zhaoyou.event.store.v1.";

    private final Class<? extends RetryableEvent> eventClass;
    private final RMapCache<String, String> map;
    private final RedissonClient redissonClient;

    public RedisEventStore(@NotNull RedissonClient client) {
        this.eventClass = null;
        this.map = null;
        this.redissonClient = client;
    }

    private RedisEventStore(@NotNull Class<? extends RetryableEvent> eventClass, @NotNull RedissonClient client) {
        this.eventClass = eventClass;
        this.map = client.getMapCache(CACHE_PREFIX.concat(eventClass.getSimpleName()), StringCodec.INSTANCE);
        this.redissonClient = client;
    }

    @Override
    public EventStore newStore(@NotNull Class<? extends RetryableEvent> eventClass) {
        return new RedisEventStore(eventClass, this.redissonClient);
    }

    @Override
    public <T extends RetryableEvent> void add(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event, int maxAttempts) {
        String key = buildKey(listenerClassName, actionMethodName, event);
        if (!this.map.containsKey(key)) {
            StoreItem item = StoreItem.create(listenerClassName, actionMethodName, event, maxAttempts);
            this.map.put(key, JsonUtils.beanToJson(item), TTL_IN_HOURS, TimeUnit.HOURS);
        }
    }

    @Override
    public List<StoreItem> list() {
        return this.map.values().stream().map(x -> JsonUtils.jsonToBean(x, StoreItem.class)).collect(Collectors.toList());
    }

    @Override
    public <T extends RetryableEvent> void remove(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event) {
        this.map.remove(buildKey(listenerClassName, actionMethodName, event));
    }

    @Override
    public <T extends RetryableEvent> void update(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event) {
        String key = buildKey(listenerClassName, actionMethodName, event);
        StoreItem item = JsonUtils.jsonToBean(this.map.get(key), StoreItem.class);
        item.setEventBody(JsonUtils.beanToJson(event));
        this.map.put(key, JsonUtils.beanToJson(item), TTL_IN_HOURS, TimeUnit.HOURS);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public Class<? extends RetryableEvent> getEventClass() {
        return this.eventClass;
    }
}
