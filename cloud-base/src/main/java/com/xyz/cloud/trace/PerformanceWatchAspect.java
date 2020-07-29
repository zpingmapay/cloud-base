package com.xyz.cloud.trace;

import com.xyz.cloud.trace.annotation.PerformanceWatch;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.Instant;

@Slf4j
@Aspect
public class PerformanceWatchAspect {
    private static final String SLOW_RESPONSE_PATTEN = "Slow API:{},arg:{},took:{}ms";

    @Around(value = "@annotation(annotation)", argNames = "pjp,annotation")
    public Object performanceWatch(ProceedingJoinPoint pjp, PerformanceWatch annotation) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String method = String.format("%s.%s", className, methodName);
        Instant startTime = Instant.now();
        try {
            return pjp.proceed();
        } finally {
            if (TimeUtils.millisElapsed(startTime) >= annotation.slowMillis()) {
                logSlowApi(method, pjp.getArgs(), startTime);
            }
        }
    }

    private void logSlowApi(String method, Object[] args, Instant start) {
        log.info(SLOW_RESPONSE_PATTEN, method, JsonUtils.beanToJson(args), TimeUtils.millisElapsed(start));
    }
}
