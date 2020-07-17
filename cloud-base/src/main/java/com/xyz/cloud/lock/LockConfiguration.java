package com.xyz.cloud.lock;

import com.xyz.cloud.lock.provider.LocalLockProvider;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.lock.provider.RedisLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

@Slf4j
public class LockConfiguration {
    @Bean
    @ConditionalOnMissingBean(value = {RedissonClient.class})
    public LockProvider ramLockProvider() {
        return new LocalLockProvider();
    }

    @Bean
    @ConditionalOnBean(RedissonClient.class)
    @ConditionalOnMissingBean(LockProvider.class)
    public LockProvider redisLockProvider(RedissonClient redissonClient) {
        return new RedisLockProvider(redissonClient);
    }

    @Bean
    public LockAspect lockAspect(LockProvider lockProvider) {
        return new LockAspect(lockProvider);
    }
}
