package com.xyz.cloud.retry.monitor;

import com.google.common.collect.Sets;
import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;
import com.xyz.cloud.retry.repository.EventRepository;
import com.xyz.cloud.retry.repository.EventRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;

import javax.validation.constraints.NotNull;
import java.util.Set;
import java.util.concurrent.Executor;

@Slf4j
public class EventRepositoryMonitor {
    private static final int ALARM_THRESHOLD = 10;
    private final ApplicationContext ctx;
    private final Executor executor;

    public EventRepositoryMonitor(ApplicationContext ctx, Executor executor) {
        this.ctx = ctx;
        this.executor = executor;
    }

    public void monitor() {
        ICache<String, EventRepository> cache = CacheManager.getLocalCache(EventRepositoryFactory.class.getName());
        cache.values().forEach(x -> {
            if (x.size() >= ALARM_THRESHOLD) {
                log.warn("monitoring repository {}, size = {}", x.getEventClass().getName(), x.size());
            } else {
                log.debug("monitoring repository {}, size = {}", x.getEventClass().getName(), x.size());
            }
            redoAll(x);
        });
    }

    private void redoAll(EventRepository repository) {
        repository.list().forEach(x -> executor.execute(() -> x.redo(ctx)));
    }
}
