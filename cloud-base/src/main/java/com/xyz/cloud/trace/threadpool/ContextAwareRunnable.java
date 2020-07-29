package com.xyz.cloud.trace.threadpool;

import org.slf4j.MDC;

import java.util.Map;

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
        if (threadContextMap != null) {
            MDC.setContextMap(threadContextMap);
        }

        try {
            task.run();
        } finally {
            try {
                MDC.clear();
            } catch (Throwable e) {
                //ignored
            }
        }
    }
}