package com.xyz.cloud.retry;

import com.google.common.collect.Sets;
import com.xyz.cloud.retry.sotre.EventStore;
import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class EventStoreMonitor {
    private final ApplicationContext ctx;

    public EventStoreMonitor(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private final Set<EventStore> set = Sets.newConcurrentHashSet();

    public void register(@NotNull EventStore store) {
        this.set.add(store);
    }

    public void monitor() {
        set.forEach(x -> CompletableFuture.runAsync(() -> redoAll(x)));
    }

    private void redoAll(EventStore store) {
        log.debug("monitoring store {}, size = {}", store.getEventClass().getName(), store.size());
        store.list().forEach(x -> redoOne(store.getEventClass(), x));
    }

    private <T extends RetryableEvent> void redoOne(Class<T> eventClass, EventStore.StoreItem item) {
        T event = JsonUtils.jsonToBean(item.getEventBody(), eventClass);
        item.redo(ctx, event);
    }
}
