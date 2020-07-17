package com.xyz.cloud.retry.config;

import com.xyz.cloud.retry.sotre.RedisEventStore;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

public class RedisStoreConfiguration {
    @Bean("RedisEventStore")
    @ConditionalOnBean(RedissonClient.class)
    public RedisEventStore redisEventStore(RedissonClient redissonClient) {
        return new RedisEventStore(redissonClient);
    }
}
