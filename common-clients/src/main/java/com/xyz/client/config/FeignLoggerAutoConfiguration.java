package com.xyz.client.config;

import com.xyz.client.FeignRemoteInfoLogger;
import feign.Logger;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author sxl
 * @since 2020/7/30 11:30
 */
@Configuration
public class FeignLoggerAutoConfiguration {

    /**
     * @return feign自定义日志打印对象
     */
    @Bean
    @ConditionalOnMissingBean(Logger.class)
    Logger feignRemoteInfoLogger() {
        return new FeignRemoteInfoLogger();
    }

    /**
     * @return 开启自定义日志打印
     */
    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.BASIC;
    }

}
