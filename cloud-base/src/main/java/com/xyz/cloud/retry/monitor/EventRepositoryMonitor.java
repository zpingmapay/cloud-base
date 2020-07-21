package com.xyz.cloud.retry.monitor;

import com.google.common.collect.Sets;
import com.xyz.cloud.retry.repository.EventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class EventRepositoryMonitor {
    private final ApplicationContext ctx;

    public EventRepositoryMonitor(ApplicationContext ctx) {
        this.ctx = ctx;
    }

    private final Set<EventRepository> set = Sets.newConcurrentHashSet();

    public void register(@NotNull EventRepository repository) {
        this.set.add(repository);
    }

    public void monitor() {
        set.forEach(x -> CompletableFuture.runAsync(() -> redoAll(x)));
    }

    private void redoAll(EventRepository repository) {
        log.debug("monitoring repository {}, size = {}", repository.getEventClass().getName(), repository.size());
        repository.list().forEach(x -> x.redo(ctx));
    }
}
