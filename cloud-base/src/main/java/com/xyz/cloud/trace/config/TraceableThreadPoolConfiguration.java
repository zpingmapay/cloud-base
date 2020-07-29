package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.threadpool.TraceableExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class TraceableThreadPoolConfiguration implements AsyncConfigurer {
    @Value("${cloud.thread.pool.name: task-pool-}")
    private String poolName;
    @Value("${cloud.thread.pool.size: 20}")
    private int poolSize;
    @Value("${cloud.thread.queue.capacity: 20}")
    private int queueCapacity;

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new TraceableExecutor();
        executor.setThreadNamePrefix(poolName);
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }

    @Override
    public Executor getAsyncExecutor() {
        return taskExecutor();
    }
}
