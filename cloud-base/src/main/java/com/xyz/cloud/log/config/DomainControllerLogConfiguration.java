package com.xyz.cloud.log.config;

import com.xyz.cloud.log.ControllerLogAspect;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

public class DomainControllerLogConfiguration {
    @Bean
    public HttpHeadersHolder domainHolder() {
        return new DomainHeadersHolder();
    }

    @Bean
    public ControllerLogAspect restLogAspect(ApplicationContext ctx, @Value("${zhaoyou.rest.log.with-header:true}") boolean logWithHeader,
                                             HttpHeadersHolder holder) {
        return new ControllerLogAspect(logWithHeader, holder);
    }
}
