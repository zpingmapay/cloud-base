package com.xyz.cloud.trace.threadpool;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class TraceableExecutor extends ThreadPoolTaskExecutor {
    @Override
    public void execute(Runnable task) {
        super.execute(ContextAwareRunnable.create(task));
    }

    @Override
    public void execute(Runnable task, long startTimeout) {
        super.execute(ContextAwareRunnable.create(task), startTimeout);
    }

    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(ContextAwareRunnable.create(task));
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(ContextAwareCallable.create(task));
    }

    @Override
    public ListenableFuture<?> submitListenable(Runnable task) {
        return super.submitListenable(ContextAwareRunnable.create(task));
    }

    @Override
    public <T> ListenableFuture<T> submitListenable(Callable<T> task) {
        return super.submitListenable(ContextAwareCallable.create(task));
    }
}
