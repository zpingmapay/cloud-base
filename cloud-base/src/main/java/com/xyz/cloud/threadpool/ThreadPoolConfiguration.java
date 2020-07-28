package com.xyz.cloud.threadpool;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class ThreadPoolConfiguration {
    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskScheduler taskScheduler(@Value("${cloud.scheduler.pool.name: job-pool-}") String jobPoolName,
                                                 @Value("${cloud.scheduler.pool.size: 20}") int jobPoolSize) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix(jobPoolName);
        taskScheduler.setPoolSize(jobPoolSize);
        return taskScheduler;
    }

    @Bean(destroyMethod = "shutdown")
    public ThreadPoolTaskExecutor taskExecutor(@Value("${cloud.thread.pool.name: task-pool-}") String poolName,
                                               @Value("${cloud.thread.pool.size: 20}") int poolSize,
                                               @Value("${cloud.thread.queue.capacity: 20}") int queueCapacity) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix(poolName);
        executor.setCorePoolSize(poolSize);
        executor.setMaxPoolSize(poolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
