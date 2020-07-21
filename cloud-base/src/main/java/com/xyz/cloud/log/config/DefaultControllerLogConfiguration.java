package com.xyz.cloud.log.config;

import com.xyz.cloud.log.ControllerLogAspect;
import com.xyz.cloud.log.holder.DefaultHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@Slf4j
public class DefaultControllerLogConfiguration {
    @Bean
    public HttpHeadersHolder defaultHolder() {
        return new DefaultHeadersHolder();
    }

    @Bean
    public ControllerLogAspect restLogAspect(ApplicationContext ctx, @Value("${cloud.log.with-header:true}") boolean logWithHeader,
                                             HttpHeadersHolder holder) {
        return new ControllerLogAspect(logWithHeader, holder);
    }
}
