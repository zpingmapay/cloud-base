package com.xyz.cloud.trace.config;

import com.xyz.cloud.lock.FailedToObtainLockException;
import com.xyz.cloud.trace.threadpool.AsyncExceptionHandler;
import com.xyz.cloud.trace.threadpool.TraceableExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@Slf4j
public class TraceableThreadPoolConfiguration implements AsyncConfigurer {
    @Value("${cloud.thread.pool.name: task-pool-}")
    private String taskPoolName;
    @Value("${cloud.scheduler.pool.name: scheduler-pool-}")
    private String schedulerPoolName;
    @Value("${cloud.thread.pool.size: 20}")
    private int poolSize;
    @Value("${cloud.thread.queue.capacity: 20}")
    private int queueCapacity;

    @ConditionalOnMissingBean(ThreadPoolTaskExecutor.class)
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new TraceableExecutor();
        executor.setThreadNamePrefix(taskPoolName);
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @ConditionalOnMissingBean(TaskScheduler.class)
    @Bean(destroyMethod = "shutdown")
    public TaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        ThreadPoolTaskScheduler scheduler = builder.build();
        scheduler.setThreadNamePrefix(schedulerPoolName);
        scheduler.setPoolSize(poolSize);
        scheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        scheduler.setErrorHandler(throwable -> {
            if(throwable instanceof FailedToObtainLockException) {
                log.info("Failed to obtain lock");
            } else {
                log.error(throwable.getMessage(), throwable);
            }
        });
        return scheduler;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new AsyncExceptionHandler();
    }
}
