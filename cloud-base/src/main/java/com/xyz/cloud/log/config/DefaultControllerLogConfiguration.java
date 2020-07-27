package com.xyz.cloud.log.config;

import com.xyz.cloud.log.ControllerLogAspect;
import com.xyz.cloud.log.holder.DefaultHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@Slf4j
public class DefaultControllerLogConfiguration {
    @Bean
    public HttpHeadersHolder<Map<String, String>> defaultHolder() {
        return new DefaultHeadersHolder();
    }

    @Bean
    public ControllerLogAspect restLogAspect(@Value("${cloud.log.with-header:true}") boolean logWithHeader,
                                             HttpHeadersHolder holder) {
        return new ControllerLogAspect(logWithHeader, holder);
    }
}
