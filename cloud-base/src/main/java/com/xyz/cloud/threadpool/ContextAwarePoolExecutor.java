package com.xyz.cloud.threadpool;

import com.google.common.collect.Maps;
import com.xyz.utils.Uuid;
import org.slf4j.MDC;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class ContextAwarePoolExecutor extends ThreadPoolTaskExecutor {
    @Override
    public void execute(Runnable task) {
        super.execute(new ContextAwareRunnable(task, getOrInitMdcCtx()));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(new ContextAwareRunnable(task, getOrInitMdcCtx()), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new ContextAwareRunnable(task, getOrInitMdcCtx()));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new ContextAwareCallable<>(task, getOrInitMdcCtx()));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(new ContextAwareRunnable(task, getOrInitMdcCtx()));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(new ContextAwareCallable<>(task, getOrInitMdcCtx()));
    }

    public static final String TID = "tid";
    public static final String UID = "uid";
    private Map<String, String> getOrInitMdcCtx() {
        Map<String, String> mdcCtx = MDC.getCopyOfContextMap();
        if (mdcCtx == null) {
            mdcCtx = Maps.newHashMap();
        }
        if (!mdcCtx.containsKey(TID)) {
            mdcCtx.put(TID, Uuid.shortUuid());
        }
        return mdcCtx;
    }

    public static class ContextAwareCallable<T> implements Callable<T> {
        private final Callable<T> task;
        private final Map<String, String> threadContextMap;

        public ContextAwareCallable(Callable<T> task, Map<String, String> threadContextMap) {
            this.task = task;
            this.threadContextMap = threadContextMap;
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

    public static class ContextAwareRunnable implements Runnable {
        private final Runnable task;
        private final Map<String, String> threadContextMap;

        public ContextAwareRunnable(Runnable task, Map<String, String> threadContextMap) {
            this.task = task;
            this.threadContextMap = threadContextMap;
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
}
