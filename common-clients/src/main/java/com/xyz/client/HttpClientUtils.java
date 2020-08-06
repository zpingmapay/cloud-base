package com.xyz.client;

import com.google.common.collect.Maps;
import com.xyz.function.TryWithCatch;
import com.xyz.utils.JsonUtils;
import com.xyz.utils.TimeUtils;
import com.xyz.utils.Uuid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.slf4j.MDC;

import java.time.Instant;
import java.util.Map;

@Slf4j
public class HttpClientUtils {
    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_TRACE_ID = "trace-id";
    public static final String HEADER_TIMESTAMP = "timestamp";
    public static final String TID = "tid";

    public static void addTraceableHeaders(HttpUriRequest request) {
        getTraceableHeaders().forEach((k, v) -> request.addHeader(k, v));
    }

    public static void addContentType(HttpUriRequest request) {
        request.addHeader(HEADER_CONTENT_TYPE, CONTENT_TYPE_JSON);
    }

    public static Map<String, String> getTraceableHeaders() {
        Map<String, String> headers = Maps.newHashMap();
        headers.put(HEADER_TIMESTAMP, String.valueOf(System.currentTimeMillis()));
        TryWithCatch.run(() -> {
            String tid = MDC.get(TID);
            tid = StringUtils.isBlank(tid) ? Uuid.shortUuid() : tid;
            headers.put(HEADER_TRACE_ID, tid);
        });
        return headers;
    }

    public static RequestConfig buildRequestConfig(int connectTimeout, int readTimeout) {
        return RequestConfig.custom()
                .setConnectionRequestTimeout(connectTimeout)
                .setConnectTimeout(connectTimeout)
                .setSocketTimeout(readTimeout).build();
    }


    static void logRequest(String url, Object request, String method) {
        log.info("请求第三方路径开始: url:{}, 参数: {}, 请求方式: {}", url, JsonUtils.beanToJson(request), method);
    }

    static <T> void logResponse(String url, T res, Instant start) {
        log.info("请求第三方路径完成: url: {}, 响应结果: {}, 耗时: {}ms", url, JsonUtils.beanToJson(res), TimeUtils.millisElapsed(start));
    }

    static void logError(String url, Exception e, Instant start) {
        log.warn("请求第三方路径失败: url: {}, 错误: {}, 耗时: {}ms", url, e.getMessage(), TimeUtils.millisElapsed(start), e);
    }

}
