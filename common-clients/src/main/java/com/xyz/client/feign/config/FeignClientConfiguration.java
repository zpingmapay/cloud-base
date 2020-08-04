package com.xyz.client.feign.config;

import com.xyz.client.HttpClientUtils;
import com.xyz.client.feign.FeignRemoteInfoLogger;
import feign.Logger;
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
    @ConditionalOnMissingBean(Logger.class)
    public Logger feignRemoteInfoLogger() {
        return new FeignRemoteInfoLogger();
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

    @Bean(destroyMethod = "close")
    @ConditionalOnMissingBean(CloseableHttpClient.class)
    public CloseableHttpClient closeableHttpClient(@Value("${cloud.client.timeout.connect:6000}") int connectTimeout,
                                                   @Value("${cloud.client.timeout.read:30000}") int readTimeout,
                                                   @Value("${cloud.client.connections.max:200}") int maxConnections,
                                                   @Value("${cloud.client.connections.per-route:20}") int maxPerRoute) {
        return HttpClientUtils.buildHttpClient(connectTimeout, readTimeout, maxConnections, maxPerRoute);
    }
}
