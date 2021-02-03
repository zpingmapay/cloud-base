package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.ControllerLogAspect;
import com.xyz.cloud.trace.PerformanceWatchAspect;
import com.xyz.cloud.trace.TraceableAspect;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceableConfiguration {
    @Bean
    public HttpHeadersHolder<DomainHeadersHolder.DomainHeader> domainHolder() {
        return new DomainHeadersHolder();
    }

    @Bean
    public ControllerLogAspect controllerLogAspect(@Value("${cloud.trace.with-header:false}") boolean withHeader,
                                               HttpHeadersHolder holder) {
        return new ControllerLogAspect(withHeader, holder);
    }

    @Bean
    public TraceableAspect traceableAspect() {
        return new TraceableAspect();
    }

    @Bean
    public PerformanceWatchAspect performanceWatchAspect() {
        return new PerformanceWatchAspect();
    }
}
