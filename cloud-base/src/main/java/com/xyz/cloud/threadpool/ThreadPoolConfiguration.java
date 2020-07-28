package com.xyz.cloud.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

@Configuration
@EnableScheduling
public class ThreadPoolConfiguration implements SchedulingConfigurer {
    @Value("${cloud.thread.pool.name: executor-pool-}")
    private String poolName;
    @Value("${cloud.thread.pool.size: 20}")
    private int poolSize;
    @Value("${cloud.thread.queue.size: 20}")
    private int queueSize;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        taskRegistrar.setScheduler(scheduler());
    }

    @Bean(destroyMethod = "shutdown")
    public Executor scheduler() {
        return Executors.newScheduledThreadPool(poolSize);
    }

    @Bean(destroyMethod = "shutdown")
    public Executor executor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(poolName);
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueSize);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }
}
