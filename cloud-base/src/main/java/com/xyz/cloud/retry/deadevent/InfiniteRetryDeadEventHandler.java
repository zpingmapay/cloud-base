package com.xyz.cloud.retry.deadevent;

import com.xyz.cloud.retry.EventStoreFactory;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.cloud.retry.sotre.EventStore;

public class InfiniteRetryDeadEventHandler implements DeadEventHandler {
    private final EventStore eventStoreTemplate;
    private final EventStoreFactory eventStoreFactory;

    public InfiniteRetryDeadEventHandler(EventStore eventStoreTemplate, EventStoreFactory eventStoreFactory) {
        this.eventStoreTemplate = eventStoreTemplate;
        this.eventStoreFactory = eventStoreFactory;
    }

    @Override
    public <T extends RetryableEvent> void handleDeadEvent(String listenerClassName, String actionMethodName, T event) {
        EventStore eventStore = eventStoreFactory.findOrCreate(event.getClass(), eventStoreTemplate.getClass());
        eventStore.add(EventStore.StoreItem.create(listenerClassName, actionMethodName, event, Integer.MAX_VALUE));
    }
}
