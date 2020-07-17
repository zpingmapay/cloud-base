package com.xyz.cloud.retry;

import com.xyz.cloud.lock.annotation.Lock;
import org.springframework.scheduling.annotation.Scheduled;

import javax.validation.constraints.NotNull;

public class EventMonitorJob {
    private final EventStoreMonitor monitor;

    public EventMonitorJob(@NotNull EventStoreMonitor monitor) {
        this.monitor = monitor;
    }

    @Lock(key = "'lock.event.store.monitor'")
    @Scheduled(cron = "*/${cloud.retry.interval-in-seconds:31} * * * * ?")
    public void monitor() {
        this.monitor.monitor();
    }

}
