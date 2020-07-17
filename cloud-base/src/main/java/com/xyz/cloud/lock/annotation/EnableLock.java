package com.xyz.cloud.lock.annotation;

import com.xyz.cloud.lock.LockConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({LockConfiguration.class})
public @interface EnableLock {
}
