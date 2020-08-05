package com.xyz.client.feign.interceptor;

import com.xyz.client.HttpClientUtils;
import feign.RequestInterceptor;
import feign.RequestTemplate;

public class TraceHeaderPropagator implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {
        HttpClientUtils.getTraceableHeaders().forEach((k, v) -> template.header(k, v));
    }
}
