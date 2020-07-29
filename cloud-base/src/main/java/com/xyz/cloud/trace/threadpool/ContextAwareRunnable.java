package com.xyz.cloud.trace.threadpool;

import lombok.Getter;

import java.util.Map;

@Getter
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
        this.consume((Void) -> task.run());
    }
}