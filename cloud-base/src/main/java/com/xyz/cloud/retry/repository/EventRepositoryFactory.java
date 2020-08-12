package com.xyz.cloud.retry.repository;

import com.xyz.cache.CacheManager;
import com.xyz.cloud.retry.RetryableEvent;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;

@Slf4j
public class EventRepositoryFactory {
    private final ApplicationContext ctx;

    public EventRepositoryFactory(@NotNull ApplicationContext ctx) {
        this.ctx = ctx;
    }

    public EventRepository findOrCreate(@NotNull Class<? extends RetryableEvent> eventClass, @NotNull Class<? extends EventRepository> repositoryClass) {
        return CacheManager.getFromLocalOrCreate(EventRepositoryFactory.class.getName(), eventClass.getName(), (k) -> {
            EventRepository templateRepository = ctx.getBean(repositoryClass);
            ValidationUtils.notNull(templateRepository, String.format("Unknown event repository type: %s", repositoryClass.getName()));
            return templateRepository.newRepository(eventClass);
        });
    }
}
