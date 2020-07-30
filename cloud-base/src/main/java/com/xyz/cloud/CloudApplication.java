package com.xyz.cloud;

import com.xyz.cloud.exceptionhandler.DefaultGlobalExceptionHandler;
import com.xyz.cloud.jwt.annotation.EnableJwt;
import com.xyz.cloud.lock.annotation.EnableLock;
import com.xyz.cloud.retry.annotation.EnableRetryableEvent;
import com.xyz.cloud.trace.annotation.EnableTraceable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Indicates a {@link Configuration configuration} class that declares one or more
 * {@link Bean @Bean} methods and also import {@link DefaultGlobalExceptionHandler advice}.
 * This is a convenience annotation that is equivalent to declaring {@code @EnableJwt},
 * {@code @EnableLock}, {@code @EnableRetryableEvent}, {@code EnableRetryableEvent}
 * and {@code EnableTraceable}.
 *
 * @author Zaiping Ma
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableJwt
@EnableLock
@EnableRetryableEvent
@EnableTraceable
@Import({DefaultGlobalExceptionHandler.class})
public @interface CloudApplication {
}
