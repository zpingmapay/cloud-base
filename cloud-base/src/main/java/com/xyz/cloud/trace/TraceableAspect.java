package com.xyz.cloud.trace;

import com.xyz.cloud.trace.annotation.Traceable;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.time.Instant;

@Slf4j
@Aspect
public class TraceableAspect {
    private static final String REQUEST_PATTEN = "{},param:{}";
    private static final String RESULT_PATTEN = "{},res:{},took:{}ms";
    private static final String ERROR_PATTEN = "{},err:{},took:{}ms";
    public static final int MAX_LOG_LENGTH = 1024;

    @Around(value = "@annotation(annotation)", argNames = "pjp,annotation")
    public Object trace(ProceedingJoinPoint pjp, Traceable annotation) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) pjp.getSignature();
        String className = methodSignature.getDeclaringType().getSimpleName();
        String methodName = methodSignature.getName();
        String method = String.format("%s.%s", className, methodName);
        Instant startTime = Instant.now();
        try {
            logRequest(method, pjp.getArgs());
            Object result = pjp.proceed();
            logResult(method, result, startTime);
            return result;
        } catch (Throwable t) {
            logError(method, t, startTime);
            throw t;
        }
    }

    private void logRequest(String method, Object[] args) {
        log.info(REQUEST_PATTEN, method, StringUtils.truncate(JsonUtils.beanToJson(args), MAX_LOG_LENGTH));
    }

    private void logResult(String method, Object result, Instant start) {
        log.info(RESULT_PATTEN, method, StringUtils.truncate(JsonUtils.beanToJson(result), MAX_LOG_LENGTH), TimeUtils.millisElapsed(start));
    }

    private void logError(String method, Throwable t, Instant start) {
        log.error(ERROR_PATTEN, method, t.getMessage(), TimeUtils.millisElapsed(start));
    }
}
