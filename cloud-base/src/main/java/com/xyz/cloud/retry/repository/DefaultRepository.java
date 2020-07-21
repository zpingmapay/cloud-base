package com.xyz.cloud.retry.repository;

import com.xyz.cache.ICache;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.stream.Collectors;

public class DefaultRepository implements EventRepository {
    public static final String CACHE_NAMESPACE = EventRepository.class.getName();
    private final Class<? extends RetryableEvent> eventClass;
    private final ICache<String, String> cache;

    public DefaultRepository(Class<? extends RetryableEvent> eventClass, @NotNull ICache<String, String> cache) {
        this.eventClass = eventClass;
        this.cache = cache;
    }

    @Override
    public EventRepository newRepository(@NotNull Class<? extends RetryableEvent> eventClass) {
        return new DefaultRepository(eventClass, this.cache);
    }

    @Override
    public void add(@NotNull EventRepository.EventItem<? extends RetryableEvent> item) {
        String key = item.getId();
        this.cache.putIfAbsent(key, JsonUtils.beanToJson(item));
    }

    @Override
    public List<EventItem<? extends RetryableEvent>> list() {
        return this.cache.values().stream().map(x -> EventItem.fromJson(x, this.eventClass)).collect(Collectors.toList());
    }

    @Override
    public void remove(@NotNull EventRepository.EventItem<? extends RetryableEvent> item) {
        this.cache.remove(item.getId());
    }

    @Override
    public void update(@NotNull EventRepository.EventItem<? extends RetryableEvent> item) {
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
