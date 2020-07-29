package com.xyz.cloud.trace.config;

import com.xyz.cloud.trace.TraceableAspect;
import com.xyz.cloud.trace.holder.DefaultHeadersHolder;
import com.xyz.cloud.trace.holder.HttpHeadersHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

import java.util.Map;

@Slf4j
public class DefaultTraceableConfiguration {
    @Bean
    public HttpHeadersHolder<Map<String, String>> defaultHolder() {
        return new DefaultHeadersHolder();
    }

    @Bean
    public TraceableAspect restLogAspect(@Value("${cloud.log.with-header:true}") boolean logWithHeader,
                                         HttpHeadersHolder holder) {
        return new TraceableAspect(logWithHeader, holder);
    }
}
