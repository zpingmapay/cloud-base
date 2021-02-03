package com.xyz.client.feign.config;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.OAuth1HttpClient;
import com.xyz.client.config.ClientCredentialConfig;
import com.xyz.client.feign.interceptor.OutboundLogger;
import com.xyz.client.feign.interceptor.TraceHeaderPropagator;
import feign.Logger;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
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

    @Bean()
    @ConditionalOnMissingBean
    public SimpleLog log(@Value("${cloud.trace.packages:com.zhaoyou}") String filterPackages) {
        return new SimpleLog(filterPackages.split(","));
    }

    @Bean
    @ConditionalOnMissingBean(Logger.class)
    public Logger outboundLogger(SimpleLog logger) {
        return new OutboundLogger(logger);
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
    @ConditionalOnMissingBean(OAuth1HttpClient.class)
    @ConditionalOnBean(CloseableHttpClient.class)
    public OAuth1HttpClient oauth1HttpClient(CloseableHttpClient httpClient,
                                             ClientCredentialConfig clientCredentialConfig) {
        return new OAuth1HttpClient(httpClient, clientCredentialConfig);
    }

    @Bean
    @ConditionalOnMissingBean(HttpClientConnectionManager.class)
    public HttpClientConnectionManager poolingConnectionManager(@Value("${cloud.client.connections.max:200}") int maxConnections,
                                                                @Value("${cloud.client.connections.per-route:20}") int maxPerRoute) {
        return HttpClientUtils.poolingConnectionManager(maxConnections, maxPerRoute);
    }
}
