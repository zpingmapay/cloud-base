package com.xyz.cloud.trace.holder;

import com.google.common.collect.Maps;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.Map;

public class DefaultHeadersHolder implements HttpHeadersHolder<Map<String, String>> {
    @Override
    public Map<String, String> extract(HttpServletRequest httpServletRequest) {
        Map<String, String> result = Maps.newHashMap();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();

        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String key = headerNames.nextElement();
                result.put(key, httpServletRequest.getHeader(key));
            }
        }
        setHeaderObject(result);
        return result;
    }

    @Override
    public String getString(String key) {
        Map<String, String> headers = getHeaderObject();
        return headers.get(key.toLowerCase());
    }
}
