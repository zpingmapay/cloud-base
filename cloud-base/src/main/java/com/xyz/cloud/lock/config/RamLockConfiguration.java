package com.xyz.cloud.lock.config;

import com.xyz.cloud.lock.LockAspect;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.lock.provider.RamLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

@Slf4j
public class RamLockConfiguration {
    @Bean
    public LockProvider ramLockProvider() {
        return new RamLockProvider();
    }

    @Bean
    public LockAspect lockAspect(LockProvider lockProvider) {
        return new LockAspect(lockProvider);
    }
}
