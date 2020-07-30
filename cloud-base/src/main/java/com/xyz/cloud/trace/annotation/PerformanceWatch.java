package com.xyz.cloud.trace.annotation;

import java.lang.annotation.*;

/**
 * Enable performance watch function on APIs may have potential performance issue
 * by {@link PerformanceWatch annotation} annotation.
 *
 * @author Zaiping Ma
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PerformanceWatch {
    /**
     * API call which is slow than {@link #slowMillis()} will be logged as a warning.
     *
     * @return the low performance bar in milliseconds.
     */
    long slowMillis() default 500;
}
