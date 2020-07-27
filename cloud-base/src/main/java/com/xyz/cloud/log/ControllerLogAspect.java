package com.xyz.cloud.log;

import com.xyz.cloud.log.holder.HttpHeadersHolder;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
public class ControllerLogAspect {
    private static final String JSON_CONTENT_TYPE = "application/json";
    private final boolean logWithHeader;
    private final HttpHeadersHolder httpHeadersHolder;

    public ControllerLogAspect(boolean logWithHeader, HttpHeadersHolder holder) {
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

    private void logRequest(String headersStr, String requestUri, Object[] args) {
        if(logWithHeader) {
            log.info("{},Uri:{}, parameters:{}", headersStr, requestUri, JsonUtils.beanToJson(args));
        } else {
            log.info("Uri:{}, parameters:{}", requestUri, JsonUtils.beanToJson(args));
        }
    }

    private void logResponse(String headersStr, String requestUri, Object result, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if(logWithHeader) {
            log.info("{},Uri:{}, return:{}, took:{}ms", headersStr, requestUri, JsonUtils.beanToJson(result), timeElapsed);
        } else {
            log.info("Uri:{}, return:{}, took:{}ms", requestUri, JsonUtils.beanToJson(result), timeElapsed);
        }
    }

    private void logError(String headersStr, String requestUri, Throwable t, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if(logWithHeader) {
            log.error("{},Uri:{}, error:{}, took:{}ms", headersStr, requestUri, t.getMessage(), timeElapsed);
        } else {
            log.error("Uri:{}, error:{}, took:{}ms", requestUri, t.getMessage(), timeElapsed);
        }
    }
}
