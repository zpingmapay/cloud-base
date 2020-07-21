package com.xyz.cloud.retry;

import com.xyz.cloud.lock.annotation.Lock;
import org.springframework.scheduling.annotation.Scheduled;

import javax.validation.constraints.NotNull;

public class EventMonitorJob {
    private final EventRepositoryMonitor monitor;

    public EventMonitorJob(@NotNull EventRepositoryMonitor monitor) {
        this.monitor = monitor;
    }

    @Lock(key = "'lock.event.repository.monitor'")
    @Scheduled(cron = "*/${cloud.retry.interval-in-seconds:59} * * * * ?")
    public void monitor() {
        this.monitor.monitor();
    }

}
