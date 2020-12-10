package com.xyz.cloud.retry;

import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;
import com.xyz.cloud.lock.FailedToObtainLockException;
import com.xyz.cloud.retry.deadevent.DeadEventHandler;
import com.xyz.cloud.retry.deadevent.DefaultDeadEventHandler;
import com.xyz.cloud.retry.monitor.EventMonitorJob;
import com.xyz.cloud.retry.monitor.EventRepositoryMonitor;
import com.xyz.cloud.retry.repository.DefaultRepository;
import com.xyz.cloud.retry.repository.EventRepository;
import com.xyz.cloud.retry.repository.EventRepositoryFactory;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.task.TaskSchedulerBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.ThreadPoolExecutor;

@EnableAsync
@EnableScheduling
@Configuration
@Slf4j
public class RetryableConfiguration {
    @Bean("RamEventRepository")
    @ConditionalOnMissingBean(RedissonClient.class)
    public EventRepository ramEventRepository() {
        ICache<String, String> cache = CacheManager.getLocalCache(EventRepository.class.getName());
        return new DefaultRepository(null, cache);
    }

    @Bean("RedisEventRepository")
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(EventRepository.class)
    public EventRepository redisEventRepository(RedissonClient redissonClient) {
        ICache<String, String> cache = CacheManager.getRedisCache(EventRepository.class.getName(), redissonClient);
        return new DefaultRepository(null, cache);
    }

    @Bean
    public EventRepositoryMonitor eventRepositoryMonitor(ApplicationContext ctx, ThreadPoolTaskExecutor executor) {
        return new EventRepositoryMonitor(ctx, executor);
    }

    @Bean
    public EventMonitorJob eventMonitorJob(EventRepositoryMonitor eventRepositoryMonitor) {
        return new EventMonitorJob(eventRepositoryMonitor);
    }

    @Bean
    public EventRepositoryFactory eventRepositoryFactory(ApplicationContext ctx) {
        return new EventRepositoryFactory(ctx);
    }

    @Bean
    @ConditionalOnMissingBean(DeadEventHandler.class)
    public DeadEventHandler deadEventHandler() {
        return new DefaultDeadEventHandler();
    }

    @Bean
    public RetryableAspect retryableAspect(EventRepositoryFactory eventRepositoryFactory, EventRepository eventRepositoryTemplate, DeadEventHandler deadEventHandler) {
        return new RetryableAspect(eventRepositoryFactory, eventRepositoryTemplate, deadEventHandler);
    }


    @Bean
    public TaskScheduler taskScheduler(TaskSchedulerBuilder builder) {
        ThreadPoolTaskScheduler scheduler = builder.build();
        scheduler.setThreadNamePrefix("scheduler-pool-");
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
}
