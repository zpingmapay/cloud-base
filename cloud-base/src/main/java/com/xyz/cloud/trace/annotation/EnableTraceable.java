package com.xyz.cloud.trace.annotation;

import com.xyz.cloud.trace.config.TraceableConfiguration;
import com.xyz.cloud.trace.config.TraceableThreadPoolConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import({TraceableConfiguration.class, TraceableThreadPoolConfiguration.class})
public @interface EnableTraceable {
}
