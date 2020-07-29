package com.xyz.cloud.trace.threadpool;

import org.slf4j.MDC;

import java.util.Map;
import java.util.concurrent.Callable;

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
        if (threadContextMap != null) {
            MDC.setContextMap(threadContextMap);
        }

        try {
            return task.call();
        } finally {
            try {
                MDC.clear();
            } catch (Throwable e) {
                //ignored
            }
        }
    }
}
