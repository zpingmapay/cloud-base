package com.xyz.cloud.log.config;

import com.xyz.cloud.log.ControllerLogAspect;
import com.xyz.cloud.log.holder.DomainHeadersHolder;
import com.xyz.cloud.log.holder.HttpHeadersHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class DomainControllerLogConfiguration {
    @Bean
    public HttpHeadersHolder<DomainHeadersHolder.DomainHeader> domainHolder() {
        return new DomainHeadersHolder();
    }

    @Bean
    public ControllerLogAspect restLogAspect(@Value("${cloud.log.with-header:false}") boolean logWithHeader,
                                             HttpHeadersHolder holder) {
        return new ControllerLogAspect(logWithHeader, holder);
    }
}
