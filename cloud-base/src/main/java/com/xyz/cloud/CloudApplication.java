package com.xyz.cloud;

import com.xyz.cloud.exceptionhandler.DefaultGlobalExceptionHandler;
import com.xyz.cloud.jwt.annotation.EnableJwt;
import com.xyz.cloud.lock.annotation.EnableLock;
import com.xyz.cloud.log.annotation.EnableControllerLog;
import com.xyz.cloud.retry.annotation.EnableRetryableEvent;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@EnableJwt
@EnableLock
@EnableRetryableEvent
@EnableControllerLog
@Import(DefaultGlobalExceptionHandler.class)
public @interface CloudApplication {
}