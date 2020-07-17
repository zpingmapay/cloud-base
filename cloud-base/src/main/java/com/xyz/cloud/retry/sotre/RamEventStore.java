package com.xyz.cloud.retry.sotre;

import com.google.common.collect.Maps;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.JsonUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RamEventStore implements EventStore {
    private final Class<? extends RetryableEvent> eventClass;
    private final Map<String, String> map = Maps.newConcurrentMap();

    public RamEventStore() {
        this.eventClass = null;
    }

    private RamEventStore(@NotNull Class<? extends RetryableEvent> eventClass) {
        this.eventClass = eventClass;
    }

    @Override
    public EventStore newStore(@NotNull Class<? extends RetryableEvent> eventClass) {
        return new RamEventStore(eventClass);
    }

    @Override
    public <T extends RetryableEvent> void add(@NotBlank String listenerClassName, @NotBlank String actionMethodName, @NotNull T event, int maxAttempts) {
        String key = buildKey(listenerClassName, actionMethodName, event);
        if (!this.map.containsKey(key)) {
            StoreItem item = StoreItem.create(listenerClassName, actionMethodName, event, maxAttempts);
            this.map.put(key, JsonUtils.beanToJson(item));
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
        this.map.put(key, JsonUtils.beanToJson(item));
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
