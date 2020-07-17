package com.xyz.cloud.retry.sotre;

import com.xyz.cache.ICache;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultStore implements EventStore {
    public static final String CACHE_NAMESPACE = EventStore.class.getName();
    private final Class<? extends RetryableEvent> eventClass;
    private final ICache<String, String> cache;

    public DefaultStore(Class<? extends RetryableEvent> eventClass, @NotNull ICache<String, String> cache) {
        this.eventClass = eventClass;
        this.cache = cache;
    }

    @Override
    public EventStore newStore(@NotNull Class<? extends RetryableEvent> eventClass) {
        return new DefaultStore(eventClass, this.cache);
    }

    @Override
    public void add(@NotNull StoreItem<? extends RetryableEvent> item) {
        String key = item.getId();
        if (!this.cache.containsKey(key)) {
            this.cache.put(key, JsonUtils.beanToJson(item));
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<StoreItem<RetryableEvent>> list() {
        return this.cache.values().stream().map(x -> StoreItem.fromJson(x, this.eventClass)).collect(Collectors.toList());
    }

    @Override
    public void remove(@NotNull StoreItem<? extends RetryableEvent> item) {
        this.cache.remove(item.getId());
    }

    @Override
    public void update(@NotNull StoreItem<? extends RetryableEvent> item) {
        String key = item.getId();
        this.cache.put(key, JsonUtils.beanToJson(item));
    }

    @Override
    public long size() {
        return this.cache.size();
    }

    @Override
    public Class<? extends RetryableEvent> getEventClass() {
        return this.eventClass;
    }
}
