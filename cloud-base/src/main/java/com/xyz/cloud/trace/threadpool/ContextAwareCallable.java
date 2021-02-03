package com.xyz.cloud.trace.threadpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.Callable;

@Getter
@Slf4j
public class ContextAwareCallable<T> implements Callable<T>, ContextAwareable {
    private final Callable<T> task;
    private final Map<String, String> threadContextMap;

    private ContextAwareCallable(Callable<T> task) {
        this.task = task;
        this.threadContextMap = this.copyOrInitMdcCtx();
    }

    public static <T> ContextAwareCallable<T> create(Callable<T> task) {
        return new ContextAwareCallable<>(task);
    }

    @Override
    public T call() throws Exception {
        return this.execute((Void t) -> task.call());
    }

    @Override
    public void handleException(Exception e) {
        log.error("Failed to handle task {}", task.getClass().getSimpleName(), e);
    }
}
