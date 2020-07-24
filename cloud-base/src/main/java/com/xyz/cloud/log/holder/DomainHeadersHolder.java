package com.xyz.cloud.log.holder;

import com.xyz.utils.BeanUtils;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.MapUtils;
import com.xyz.utils.ValidationUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

import static com.xyz.cloud.jwt.JwtTokenProvider.USER_ID;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class DomainHeadersHolder implements HttpHeadersHolder {
    private static final String HEADER_TRACE_ID = "trace-id";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_LNG = "lng";
    private static final String HEADER_LAT = "lat";
    private static final String HEADER_APP_ID = "app-id";
    private static final ThreadLocal<Map<String, Object>> headerThreadLocal = new ThreadLocal<>();

    @Override
    public Object extract(HttpServletRequest request) {
        DomainHeader domainHeader = new DomainHeader();
        domainHeader.setTraceId(request.getHeader(HEADER_TRACE_ID));
        domainHeader.setTimestamp(request.getHeader(HEADER_TIMESTAMP));
        if (StringUtils.isNotBlank(request.getHeader(HEADER_LNG))) {
            domainHeader.setLng(Double.parseDouble(request.getHeader(HEADER_LNG)));
        }
        if (StringUtils.isNotBlank(request.getHeader(HEADER_LAT))) {
            domainHeader.setLat(Double.parseDouble(request.getHeader(HEADER_LAT)));
        }
        domainHeader.setAppId(request.getHeader(HEADER_APP_ID));
        domainHeader.setUserId(getUserIdFromCtx());
        setHeaderObject(domainHeader);
        return domainHeader;
    }

    @Override
    public String getString(String key) {
        ValidationUtils.notBlank(key, "Key is required");

        Map<String, Object> headers = headerThreadLocal.get();
        if(headers == null) {
            Object headerObject = this.getHeaderObject();

            headers = MapUtils.transform(BeanUtils.beanToMap(headerObject), this::normalize, v ->v);
            headerThreadLocal.set(headers);
        }

        String normalizedKey = normalize(key);
        if (!headers.containsKey(normalizedKey)) {
            return null;
        }
        return headers.get(normalizedKey).toString();
    }

    private String normalize(String key) {
        return key.toLowerCase().replaceAll("-", "");
    }

    private String getUserIdFromCtx() {
        return (String) Objects.requireNonNull(RequestContextHolder.getRequestAttributes()).getAttribute(USER_ID, SCOPE_REQUEST);
    }

    @Data
    public static class DomainHeader {
        private double lng;
        private double lat;
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
