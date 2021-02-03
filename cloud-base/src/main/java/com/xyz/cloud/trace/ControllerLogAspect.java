package com.xyz.cloud.trace;

import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import com.xyz.log.SimpleLog;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.time.Instant;

import static com.xyz.cloud.trace.TraceableAspect.MAX_LOG_LENGTH;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

@Slf4j
@Aspect
@RequiredArgsConstructor
public class ControllerLogAspect {
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String REQUEST_PATTEN_WITHOUT_HEARERS = "URI:{}, param:{}";
    private static final String REQUEST_PATTEN_WITH_HEARERS = "{}," + REQUEST_PATTEN_WITHOUT_HEARERS;
    private static final String RESPONSE_PATTEN_WITHOUT_HEARERS = "URI:{}, res:{}, took:{}ms";
    private static final String RESPONSE_PATTEN_WITH_HEARERS = "{}," + RESPONSE_PATTEN_WITHOUT_HEARERS;
    private static final String ERROR_PATTEN_WITHOUT_HEARERS = "URI:{}, err:{}, took:{}ms";
    private static final String ERROR_PATTEN_WITH_HEARERS = "{}," + ERROR_PATTEN_WITHOUT_HEARERS;

    private final boolean logWithHeader;
    private final HttpHeadersHolder httpHeadersHolder;
    private final SimpleLog logger;


    @Around("@annotation(org.springframework.web.bind.annotation.PostMapping)||@annotation(org.springframework.web.bind.annotation.GetMapping)")
    public Object logAround(ProceedingJoinPoint pjp) throws Throwable {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) getRequestAttributes();
        if (requestAttributes == null) {
            return proceedWithoutLog(pjp);
        }
        HttpServletRequest request = requestAttributes.getRequest();
        Object headers = httpHeadersHolder.extract(request);

        try {
            String contentType = request.getContentType();
            if (contentType == null || !contentType.toLowerCase().startsWith(JSON_CONTENT_TYPE) || !isRestController(pjp)) {
                return proceedWithoutLog(pjp);
            }
            return proceedWithLog(pjp, request, headers);
        } catch (Throwable e) {
            throw e;
        } finally {
            httpHeadersHolder.removeHeaderObject();
        }
    }

    private boolean isRestController(ProceedingJoinPoint pjp) {
        return pjp.getTarget().getClass().isAnnotationPresent(RestController.class);
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
        }
    }

    private void logRequest(String headersStr, String requestUri, Object[] args) {
        if (logWithHeader) {
            log.info(REQUEST_PATTEN_WITH_HEARERS, headersStr, requestUri, StringUtils.truncate(JsonUtils.beanToJson(args), MAX_LOG_LENGTH));
        } else {
            log.info(REQUEST_PATTEN_WITHOUT_HEARERS, requestUri, StringUtils.truncate(JsonUtils.beanToJson(args), MAX_LOG_LENGTH));
        }
    }

    private void logResponse(String headersStr, String requestUri, Object result, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if (logWithHeader) {
            log.info(RESPONSE_PATTEN_WITH_HEARERS, headersStr, requestUri, StringUtils.truncate(JsonUtils.beanToJson(result), MAX_LOG_LENGTH), timeElapsed);
        } else {
            log.info(RESPONSE_PATTEN_WITHOUT_HEARERS, requestUri, StringUtils.truncate(JsonUtils.beanToJson(result), MAX_LOG_LENGTH), timeElapsed);
        }
    }

    private void logError(String headersStr, String requestUri, Throwable t, Instant start) {
        long timeElapsed = TimeUtils.millisElapsed(start);
        if (logWithHeader) {
            logger.error(log, ERROR_PATTEN_WITH_HEARERS, t, headersStr, requestUri, t.getMessage(), timeElapsed);
        } else {
            logger.error(log, ERROR_PATTEN_WITHOUT_HEARERS, t, requestUri, t.getMessage(), timeElapsed);
        }
    }
}
