package com.xyz.cloud.trace.threadpool;

import com.xyz.cloud.lock.FailedToObtainLockException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class AsyncExceptionHandler implements AsyncUncaughtExceptionHandler {
    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... params) {
        if(throwable instanceof FailedToObtainLockException) {
            log.info("Failed to obtain lock {}.{}", method.getDeclaringClass().getSimpleName(), method.getName());
        } else {
            log.error(throwable.getMessage(), throwable);
        }
    }
}