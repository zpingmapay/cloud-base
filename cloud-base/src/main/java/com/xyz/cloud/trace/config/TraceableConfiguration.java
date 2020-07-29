package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.TraceableAspect;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

public class TraceableConfiguration {
    @Bean
    public HttpHeadersHolder<DomainHeadersHolder.DomainHeader> domainHolder() {
        return new DomainHeadersHolder();
    }

    @Bean
    public TraceableAspect restLogAspect(@Value("${cloud.log.with-header:false}") boolean logWithHeader,
                                         HttpHeadersHolder holder) {
        return new TraceableAspect(logWithHeader, holder);
    }
}
