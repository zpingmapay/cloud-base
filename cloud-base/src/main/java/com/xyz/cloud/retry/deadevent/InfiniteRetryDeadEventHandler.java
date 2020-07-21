package com.xyz.cloud.retry.deadevent;

import com.xyz.cloud.retry.repository.EventRepositoryFactory;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.cloud.retry.repository.EventRepository;

public class InfiniteRetryDeadEventHandler implements DeadEventHandler {
    private final EventRepository eventRepositoryTemplate;
    private final EventRepositoryFactory eventRepositoryFactory;

    public InfiniteRetryDeadEventHandler(EventRepository eventRepositoryTemplate, EventRepositoryFactory eventRepositoryFactory) {
        this.eventRepositoryTemplate = eventRepositoryTemplate;
        this.eventRepositoryFactory = eventRepositoryFactory;
    }

    @Override
    public <T extends RetryableEvent> void handleDeadEvent(String listenerClassName, String actionMethodName, T event) {
        EventRepository eventRepository = eventRepositoryFactory.findOrCreate(event.getClass(), eventRepositoryTemplate.getClass());
        eventRepository.add(EventRepository.EventItem.create(listenerClassName, actionMethodName, event, Integer.MAX_VALUE));
    }
}
