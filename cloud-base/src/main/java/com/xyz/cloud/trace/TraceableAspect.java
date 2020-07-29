package com.xyz.cloud.trace;

import com.xyz.cloud.trace.annotation.Traceable;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
public class TraceableAspect {
    private static final String JSON_CONTENT_TYPE = "application/json";
    private final boolean logWithHeader;
    private final HttpHeadersHolder httpHeadersHolder;

    public TraceableAspect(boolean logWithHeader, HttpHeadersHolder holder) {
        this.logWithHeader = logWithHeader;
        this.httpHeadersHolder = holder;
    }

    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)||@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        if (requestAttributes == null) {
            return proceedWithoutLog(pjp);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        String contentType = request.getContentType();

        if (contentType == null || !contentType.toLowerCase().startsWith(JSON_CONTENT_TYPE)) {
            return proceedWithoutLog(pjp);
        }

        Object headers = httpHeadersHolder.extract(request);
        return proceedWithLog(pjp, request, headers);
    }

    @Around(value = "@annotation(annotation) || @within(annotation)", argNames = "pjp,annotation")
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

    private Object proceedWithoutLog(ProceedingJoinPoint pjp) throws Throwable {
        return pjp.proceed();
    }

    private Object proceedWithLog(ProceedingJoinPoint pjp, HttpServletRequest request, Object headers) throws Throwable {
        Instant start = Instant.now();
        String requestUri = request.getRequestURI();
        String headersStr = headers.toString();
        try {
            logRequest(headersStr, requestUri, pjp.getArgs());
            Object result = pjp.proceed();
            logResponse(headersStr, requestUri, result, start);
            return result;
        } catch (Exception e) {
            logError(headersStr, requestUri, e, start);
            throw e;
        } finally {
            httpHeadersHolder.removeHeaderObject();
        }
    }

    private static final String REQUEST_PATTEN = "{},param:{}";

    private void logRequest(String method, Object[] args) {
        log.info(REQUEST_PATTEN, method, JsonUtils.beanToJson(args));
    }

    private static final String RESULT_PATTEN = "{},result:{},took:{}ms";

    private void logResult(String method, Object result, Instant start) {
        log.info(RESULT_PATTEN, method, JsonUtils.beanToJson(result), TimeUtils.millisElapsed(start));
    }

    private static final String ERROR_PATTEN = "{},error:{},took:{}ms";

    private void logError(String method, Throwable t, Instant start) {
        log.error(ERROR_PATTEN, method, t.getMessage(), TimeUtils.millisElapsed(start));
    }

    private static final String REQUEST_PATTEN_WITH_HEARERS = "{},URI:{}, param:{}";
    private static final String REQUEST_PATTEN_WITHOUT_HEARERS = "URI:{}, param:{}";

    private void logRequest(String headersStr, String requestUri, Object[] args) {
        if (logWithHeader) {
            log.info(REQUEST_PATTEN_WITH_HEARERS, headersStr, requestUri, JsonUtils.beanToJson(args));
        } else {
            log.info(REQUEST_PATTEN_WITHOUT_HEARERS, requestUri, JsonUtils.beanToJson(args));
        }
    }

    private static final String RESPONSE_PATTEN_WITH_HEARERS = "{},URI:{}, return:{}, took:{}ms";
    private static final String RESPONSE_PATTEN_WITHOUT_HEARERS = "URI:{}, return:{}, took:{}ms";

    private void logResponse(String headersStr, String requestUri, Object result, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if (logWithHeader) {
            log.info(RESPONSE_PATTEN_WITH_HEARERS, headersStr, requestUri, JsonUtils.beanToJson(result), timeElapsed);
        } else {
            log.info(RESPONSE_PATTEN_WITHOUT_HEARERS, requestUri, JsonUtils.beanToJson(result), timeElapsed);
        }
    }

    private static final String ERROR_PATTEN_WITH_HEARERS = "{},URI:{}, error:{}, took:{}ms";
    private static final String ERROR_PATTEN_WITHOUT_HEARERS = "URI:{}, error:{}, took:{}ms";

    private void logError(String headersStr, String requestUri, Throwable t, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if (logWithHeader) {
            log.error(ERROR_PATTEN_WITH_HEARERS, headersStr, requestUri, t.getMessage(), timeElapsed);
        } else {
            log.error(ERROR_PATTEN_WITHOUT_HEARERS, requestUri, t.getMessage(), timeElapsed);
        }
    }
}
