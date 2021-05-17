package com.xyz.cloud.trace.holder;

import com.xyz.function.TryWithCatch;
import com.xyz.utils.*;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.xyz.cloud.jwt.JwtTokenProvider.USER_ID;
import static com.xyz.cloud.trace.threadpool.ContextAwareable.TID;
import static com.xyz.cloud.trace.threadpool.ContextAwareable.UID;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class DomainHeadersHolder implements HttpHeadersHolder<DomainHeadersHolder.DomainHeader> {
    private static final String HEADER_TRACE_ID = "trace-id";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_LNG = "lng";
    private static final String HEADER_LAT = "lat";
    private static final String HEADER_APP_ID = "app-id";
    private static final String DEFAULT_APP_ID = String.valueOf(Integer.MIN_VALUE);

    private static final ThreadLocal<Map<String, Object>> headerThreadLocal = new ThreadLocal<>();

    @Override
    public DomainHeader extract(HttpServletRequest request) {
        DomainHeader domainHeader = new DomainHeader();
        domainHeader.setAppId(this.getHeader(request, HEADER_APP_ID, DEFAULT_APP_ID));
        domainHeader.setTraceId(this.getHeader(request, HEADER_TRACE_ID, Uuid.generate()));
        domainHeader.setTimestamp(this.getHeader(request, HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis())));
        domainHeader.setLng(request.getHeader(HEADER_LNG));
        domainHeader.setLat(request.getHeader(HEADER_LAT));
        String userId = getUserIdFromCtx();
        domainHeader.setUserId(userId);
        setHeaderObject(domainHeader);

        MDC.put(TID, domainHeader.getTraceId());
        if (StringUtils.isNotBlank(userId)) {
            MDC.put(UID, userId);
        }
        return domainHeader;
    }

    @Override
    public String getString(String key) {
        ValidationUtils.notBlank(key, "Key is required");

        Map<String, Object> headers = headerThreadLocal.get();
        if (headers == null) {
            Object headerObject = this.getHeaderObject();

            headers = MapUtils.transform(BeanUtils.beanToMap(headerObject), this::normalize, v -> v);
            headerThreadLocal.set(headers);
        }

        String normalizedKey = normalize(key);
        if (!headers.containsKey(normalizedKey)) {
            return null;
        }
        return headers.get(normalizedKey).toString();
    }

    @Override
    public void removeHeaderObject() {
        TryWithCatch.run(() -> {
            HttpHeadersHolder.super.removeHeaderObject();
            headerThreadLocal.remove();
            MDC.remove(TID);
            MDC.remove(UID);
        });
    }

    private String getHeader(HttpServletRequest request, String key, String defaultValue) {
        String header = request.getHeader(key);
        if(StringUtils.isBlank(header)) {
            header = request.getHeader(normalize(key));
        }
        return StringUtils.isBlank(header) ? defaultValue : header;
    }

    private String normalize(String key) {
        return key.toLowerCase().replaceAll("-", "");
    }

    private String getUserIdFromCtx() {
        return (String) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(USER_ID, SCOPE_REQUEST);
    }

    @Data
    public static class DomainHeader {
        private String lng;
        private String lat;
        private String traceId;
        private String timestamp;
        private String appId;
        private String userId;

        @Override
        public String toString() {
            return JsonUtils.beanToJson(this);
        }
    }
}
