package com.xyz.cloud.retry;

import com.xyz.cache.CacheManager;
import com.xyz.cache.ICache;
import com.xyz.cloud.retry.sotre.DefaultStore;
import com.xyz.cloud.retry.sotre.EventStore;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@Configuration
public class RetryableConfiguration {
    @Bean("RamEventStore")
    @ConditionalOnMissingBean(value = {RedissonClient.class})
    public EventStore ramEventStore() {
        ICache<String, String> cache = CacheManager.getLocalCache(DefaultStore.CACHE_NAMESPACE);
        return new DefaultStore(null, cache);
    }

    @Bean("RedisEventStore")
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(EventStore.class)
    public EventStore redisEventStore(RedissonClient redissonClient) {
        ICache<String, String> cache = CacheManager.getRedisCache(DefaultStore.CACHE_NAMESPACE, redissonClient);
        return new DefaultStore(null, cache);
    }

    @Bean
    public EventStoreMonitor eventStoreMonitor(ApplicationContext ctx) {
        return new EventStoreMonitor(ctx);
    }

    @Bean
    public EventMonitorJob eventStoreMonitorJob(EventStoreMonitor eventStoreMonitor) {
        return new EventMonitorJob(eventStoreMonitor);
    }

    @Bean
    public EventStoreFactory eventStoreFactory(EventStoreMonitor eventStoreMonitor, ApplicationContext ctx) {
        return new EventStoreFactory(eventStoreMonitor, ctx);
    }

    @Bean
    public RetryableAspect retryableAspect(EventStoreFactory eventStoreFactory, EventStore eventStoreTemplate) {
        return new RetryableAspect(eventStoreFactory, eventStoreTemplate);
    }
}
