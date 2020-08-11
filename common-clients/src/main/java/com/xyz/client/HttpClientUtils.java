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
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.MDC;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Map;

@Slf4j
public class HttpClientUtils {
    private static final String REQUEST_LOG_PATTEN = "请求第三方开始: url:{}, 参数: {}, 方法: {}";
    private static final String RESPONSE_LOG_PATTEN = "请求第三方完成: url: {}, 响应: {}, 耗时: {}ms";
    private static final String ERROR_LOG_PATTEN = "请求第三方失败: 错误: {}, 耗时: {}ms";

    public static final String HEADER_CONTENT_TYPE = "content-type";
    public static final String CONTENT_TYPE_JSON = "application/json";
    public static final String HEADER_TRACE_ID = "trace-id";
    public static final String HEADER_TIMESTAMP = "timestamp";
    public static final String TID = "tid";

    public static void addTraceableHeaders(HttpUriRequest request) {
        getTraceableHeaders().forEach(request::addHeader);
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

    public static CloseableHttpClient buildHttpClient(int connectTimeout,int readTimeout, int maxConnections, int maxPerRoute) {
        return buildHttpClient(connectTimeout, readTimeout, poolingConnectionManager(maxConnections, maxPerRoute));
    }

    public static CloseableHttpClient buildHttpClient(int connectTimeout, int readTimeout, HttpClientConnectionManager connectionManager) {
        return HttpClients.custom()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeout, readTimeout))
                .setConnectionManager(connectionManager)
                .build();
    }

    public static void logRequest(String url, Object request, String method) {
        log.info(REQUEST_LOG_PATTEN, url, JsonUtils.beanToJson(request), method);
    }

    public static <T> void logResponse(String url, T res, Instant start) {
        log.info(RESPONSE_LOG_PATTEN, url, JsonUtils.beanToJson(res), TimeUtils.millisElapsed(start));
    }

    public static void logError(Exception e, Instant start) {
        log.warn(ERROR_LOG_PATTEN, e.getMessage(), TimeUtils.millisElapsed(start), e);
    }

    public static PoolingHttpClientConnectionManager poolingConnectionManager(int maxConnections, int maxPerRoute) {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.warn("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }
        SSLConnectionSocketFactory sslSf = null;
        try {
            sslSf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.warn("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        RegistryBuilder<ConnectionSocketFactory> registryBuilder = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", new PlainConnectionSocketFactory());
        if (sslSf != null) {
            registryBuilder.register("https", sslSf);
        }

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(registryBuilder.build());
        poolingConnectionManager.setMaxTotal(maxConnections);
        poolingConnectionManager.setDefaultMaxPerRoute(maxPerRoute);

        return poolingConnectionManager;
    }
}
