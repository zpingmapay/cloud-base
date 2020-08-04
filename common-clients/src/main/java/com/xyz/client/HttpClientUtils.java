package com.xyz.client;

import com.google.common.collect.Maps;
import com.xyz.function.TryWithCatch;
import com.xyz.utils.Uuid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.config.RegistryBuilder;
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
import java.util.Map;

@Slf4j
public class HttpClientUtils {
    public static final String HEADER_TRACE_ID = "trace-id";
    public static final String HEADER_TIMESTAMP = "timestamp";
    public static final String TID = "tid";

    public static void addTraceableHeaders(HttpUriRequest requestBase) {
        getTraceableHeaders().forEach((k, v) -> requestBase.addHeader(k, v));
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
        return HttpClients.custom()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeout, readTimeout))
                .setConnectionManager(poolingConnectionManager(maxConnections, maxPerRoute))
                .build();
    }

    private static PoolingHttpClientConnectionManager poolingConnectionManager(int maxConnections, int maxPerRoute) {
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
