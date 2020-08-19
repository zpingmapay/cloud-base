package com.xyz.cloud.lock.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Lock {
    String key();

    long waitInMillis() default 0;

    long lockInMillis() default 30 * 1000L;
}
