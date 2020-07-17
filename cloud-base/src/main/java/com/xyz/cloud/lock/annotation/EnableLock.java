package com.xyz.cloud.lock.annotation;

import com.xyz.cloud.lock.config.LockConfigSelector;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.cloud.lock.provider.RamLockProvider;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({LockConfigSelector.class})
public @interface EnableLock {
    Class<? extends LockProvider> provider() default RamLockProvider.class;
}
