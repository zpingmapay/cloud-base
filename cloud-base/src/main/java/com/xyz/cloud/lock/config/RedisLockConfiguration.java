package com.xyz.cloud.lock.config;

import com.xyz.cloud.lock.LockAspect;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.lock.provider.RedisLockProvider;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

public class RedisLockConfiguration {
    @Bean
    @ConditionalOnBean(RedissonClient.class)
    public LockProvider redisLockProvider(RedissonClient redissonClient) {
        return new RedisLockProvider(redissonClient);
    }

    @Bean
    public LockAspect lockAspect(RedisLockProvider lockProvider) {
        return new LockAspect(lockProvider);
    }

}
