package com.xyz.cloud.retry.annotation;

import com.xyz.cloud.retry.RetryableConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RetryableConfiguration.class})
public @interface EnableRetryableEvent {
}
