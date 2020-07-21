package com.xyz.cloud.retry;

import com.google.common.collect.Maps;
import com.xyz.cloud.retry.sotre.EventStore;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
public class EventStoreFactory {
    private final EventStoreMonitor eventStoreMonitor;
    private final ApplicationContext ctx;
    private final Map<String, EventStore> cachedStores = Maps.newConcurrentMap();

    public EventStoreFactory(@NotNull EventStoreMonitor eventStoreMonitor, @NotNull ApplicationContext ctx) {
        this.eventStoreMonitor = eventStoreMonitor;
        this.ctx = ctx;
    }

    public EventStore findOrCreate(@NotNull Class<? extends RetryableEvent> eventClass, @NotNull Class<? extends EventStore> storeClass) {
        synchronized (eventClass.getName()) {
            EventStore store = cachedStores.get(eventClass.getName());
            if (store == null) {
                EventStore templateStore = ctx.getBean(storeClass);
                ValidationUtils.notNull(templateStore, String.format("Unknown store type: %s", storeClass.getName()));
                store = templateStore.newStore(eventClass);
                eventStoreMonitor.register(store);
                cachedStores.put(eventClass.getName(), store);
            }
            return store;
        }
    }
}
