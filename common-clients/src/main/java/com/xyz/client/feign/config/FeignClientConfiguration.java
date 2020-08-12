package com.xyz.client.feign.config;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.feign.interceptor.OutboundLogger;
import com.xyz.client.feign.interceptor.TraceHeaderPropagator;
import feign.Logger;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sxl
 * @since 2020/7/30 11:30
 */
@Configuration
public class FeignClientConfiguration {
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
        return Logger.Level.BASIC;
    }


    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient httpClient(@Value("${cloud.client.timeout.connect:6000}") int connectTimeout,
                                          @Value("${cloud.client.timeout.read:30000}") int readTimeout,
                                          HttpClientConnectionManager connectionManager) {
        return HttpClientUtils.buildHttpClient(connectTimeout, readTimeout, connectionManager);
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientConnectionManager.class)
    public HttpClientConnectionManager poolingConnectionManager(@Value("${cloud.client.connections.max:200}") int maxConnections,
                                                                @Value("${cloud.client.connections.per-route:20}") int maxPerRoute) {
        return HttpClientUtils.poolingConnectionManager(maxConnections, maxPerRoute);
    }
}
