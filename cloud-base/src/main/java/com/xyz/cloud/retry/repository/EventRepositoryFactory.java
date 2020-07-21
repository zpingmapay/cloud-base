package com.xyz.cloud.retry.repository;

import com.google.common.collect.Maps;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.cloud.retry.monitor.EventRepositoryMonitor;
import com.xyz.cloud.retry.repository.EventRepository;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Slf4j
public class EventRepositoryFactory {
    private final EventRepositoryMonitor eventRepositoryMonitor;
    private final ApplicationContext ctx;
    private final Map<String, EventRepository> cachedRepositories = Maps.newConcurrentMap();

    public EventRepositoryFactory(@NotNull EventRepositoryMonitor eventRepositoryMonitor, @NotNull ApplicationContext ctx) {
        this.eventRepositoryMonitor = eventRepositoryMonitor;
        this.ctx = ctx;
    }

    public EventRepository findOrCreate(@NotNull Class<? extends RetryableEvent> eventClass, @NotNull Class<? extends EventRepository> repositoryClass) {
        synchronized (eventClass.getName()) {
            EventRepository repository = cachedRepositories.get(eventClass.getName());
            if (repository == null) {
                EventRepository templateRepository = ctx.getBean(repositoryClass);
                ValidationUtils.notNull(templateRepository, String.format("Unknown event repository type: %s", repositoryClass.getName()));
                repository = templateRepository.newRepository(eventClass);
                eventRepositoryMonitor.register(repository);
                cachedRepositories.put(eventClass.getName(), repository);
            }
            return repository;
        }
    }
}
