package com.xyz.cloud.retry.annotation;

import com.xyz.cloud.retry.config.RetryConfigSelector;
import com.xyz.cloud.retry.sotre.EventStore;
import com.xyz.cloud.retry.sotre.RamEventStore;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({RetryConfigSelector.class})
public @interface EnableRetryableEvent {
    Class<? extends EventStore> store() default RamEventStore.class;
}
