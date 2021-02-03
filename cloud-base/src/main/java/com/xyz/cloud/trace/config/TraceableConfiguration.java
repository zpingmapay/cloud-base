package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.ControllerLogAspect;
import com.xyz.cloud.trace.PerformanceWatchAspect;
import com.xyz.cloud.trace.TraceableAspect;
import com.xyz.cloud.trace.holder.DomainHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TraceableConfiguration {
    @Bean
    public HttpHeadersHolder<DomainHeadersHolder.DomainHeader> domainHolder() {
        return new DomainHeadersHolder();
    }

    @Bean()
    @ConditionalOnMissingBean
    public SimpleLog log(@Value("${cloud.trace.packages:com.zhaoyou}") String filterPackages) {
        return new SimpleLog(filterPackages.split(","));
    }

    @Bean
    public ControllerLogAspect controllerLogAspect(@Value("${cloud.trace.with-header:false}") boolean withHeader,
                                               HttpHeadersHolder holder, SimpleLog logger) {
        return new ControllerLogAspect(withHeader, holder, logger);
    }

    @Bean
    public TraceableAspect traceableAspect(SimpleLog logger) {
        return new TraceableAspect(logger);
    }

    @Bean
    public PerformanceWatchAspect performanceWatchAspect() {
        return new PerformanceWatchAspect();
    }
}
