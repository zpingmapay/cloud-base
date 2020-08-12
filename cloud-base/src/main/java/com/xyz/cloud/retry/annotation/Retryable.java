package com.xyz.cloud.retry.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Retryable {
    int maxAttempts() default 100;
}
