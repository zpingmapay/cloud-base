package com.xyz.cloud.trace.threadpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@Slf4j
public class ContextAwareRunnable implements Runnable, ContextAwareable {
    private final Runnable task;
    private final Map<String, String> threadContextMap;

    private ContextAwareRunnable(Runnable task) {
        this.task = task;
        this.threadContextMap = this.copyOrInitMdcCtx();
    }

    public static ContextAwareRunnable create(Runnable task) {
        return new ContextAwareRunnable(task);
    }

    @Override
    public void run() {
        this.execute(task::run);
    }

    @Override
    public void handleException(Exception e) {
        log.error("Failed to handle task {}", task.getClass().getSimpleName(), e);
    }
}