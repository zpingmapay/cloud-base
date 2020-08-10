package com.xyz.client.feign.config;

import com.xyz.client.feign.interceptor.OutboundLogger;
import com.xyz.client.feign.interceptor.TraceHeaderPropagator;
import feign.Logger;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HeaderElement;
import org.apache.http.HeaderElementIterator;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHeaderElementIterator;
import org.apache.http.protocol.HTTP;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

import static com.xyz.client.HttpClientUtils.buildRequestConfig;

/**
 * @author sxl
 * @since 2020/7/30 11:30
 */
@Slf4j
@Configuration
public class FeignClientConfiguration {

    private static final int DEFAULT_KEEP_ALIVE_TIME_MILLIS = 20 * 1000;

    @Bean
    public TraceHeaderPropagator traceHeaderPropagator() {
        return new TraceHeaderPropagator();
    }

    @Bean
    @ConditionalOnMissingBean(Logger.class)
    public Logger outboundLogger() {
        return new OutboundLogger();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient httpClient(@Value("${cloud.client.timeout.connect:6000}") int connectTimeout,
                                          @Value("${cloud.client.timeout.read:30000}") int readTimeout,
                                          @Value("${cloud.client.connections.max:200}") int maxConnections,
                                          @Value("${cloud.client.connections.per-route:20}") int maxPerRoute) {
        return buildHttpClient(connectTimeout, readTimeout, maxConnections, maxPerRoute);
    }


    @Bean
    public PoolingHttpClientConnectionManager poolingConnectionManager(@Value("${cloud.client.connections.max:200}") int maxConnections,
                                                                       @Value("${cloud.client.connections.per-route:20}") int maxPerRoute) {
        SSLContextBuilder builder = new SSLContextBuilder();
        try {
            builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
        } catch (NoSuchAlgorithmException | KeyStoreException e) {
            log.warn("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }
        SSLConnectionSocketFactory sslsf = null;
        try {
            sslsf = new SSLConnectionSocketFactory(builder.build());
        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            log.error("Pooling Connection Manager Initialisation failure because of " + e.getMessage(), e);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder
                .<ConnectionSocketFactory>create().register("https", sslsf)
                .register("http", new PlainConnectionSocketFactory())
                .build();

        PoolingHttpClientConnectionManager poolingConnectionManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
        poolingConnectionManager.setMaxTotal(maxConnections);
        poolingConnectionManager.setDefaultMaxPerRoute(maxPerRoute);
        return poolingConnectionManager;
    }


    @Bean
    public ConnectionKeepAliveStrategy connectionKeepAliveStrategy() {
        return (response, context) -> {
            HeaderElementIterator it = new BasicHeaderElementIterator
                    (response.headerIterator(HTTP.CONN_KEEP_ALIVE));
            while (it.hasNext()) {
                HeaderElement he = it.nextElement();
                String param = he.getName();
                String value = he.getValue();

                if (value != null && param.equalsIgnoreCase("timeout")) {
                    return Long.parseLong(value) * 1000;
                }
            }
            return DEFAULT_KEEP_ALIVE_TIME_MILLIS;
        };
    }

    private CloseableHttpClient buildHttpClient(int connectTimeout, int readTimeout, int maxConnections, int maxPerRoute) {
        return HttpClients.custom()
                .setDefaultRequestConfig(buildRequestConfig(connectTimeout, readTimeout))
                .evictIdleConnections(30, TimeUnit.SECONDS)
                .setConnectionManager(poolingConnectionManager(maxConnections, maxPerRoute))
                .setKeepAliveStrategy(connectionKeepAliveStrategy())
                .build();
    }
}
