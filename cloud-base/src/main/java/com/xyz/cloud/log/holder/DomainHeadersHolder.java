package com.xyz.cloud.log.holder;

import com.xyz.utils.BeanUtils;
import com.xyz.utils.JsonUtils;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

public class DomainHeadersHolder implements HttpHeadersHolder {
    private static final String HEADER_TRACE_ID = "trace-id";
    private static final String HEADER_TIMESTAMP = "timestamp";
    private static final String HEADER_LNG = "lng";
    private static final String HEADER_LAT = "lat";
    private static final String HEADER_APP_ID = "app-id";
    public static final String USER_ID = "x-zhaoyou-id";
  
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
        Object headerObject = this.getHeaderObject();
        return BeanUtils.beanToMap(headerObject).get(key).toString();
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
