package com.xyz.cloud.retry.config;

import com.xyz.cloud.retry.EventMonitorJob;
import com.xyz.cloud.retry.EventStoreFactory;
import com.xyz.cloud.retry.EventStoreMonitor;
import com.xyz.cloud.retry.RetryableAspect;
import com.xyz.cloud.retry.sotre.EventStore;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@Configuration
public class RetryableConfiguration {
    @Bean
    public EventStoreMonitor eventStoreMonitor(ApplicationContext ctx) {
        return new EventStoreMonitor(ctx);
    }

    @Bean
    public EventMonitorJob eventStoreMonitorJob(EventStoreMonitor eventStoreMonitor) {
        return new EventMonitorJob(eventStoreMonitor);
    }

    @Bean
    public EventStoreFactory eventStoreFactory(EventStoreMonitor eventStoreMonitor, ApplicationContext ctx) {
        return new EventStoreFactory(eventStoreMonitor, ctx);
    }

    @Bean
    public RetryableAspect retryableAspect(EventStoreFactory eventStoreFactory, EventStore eventStoreTemplate) {
        return new RetryableAspect(eventStoreFactory, eventStoreTemplate);
    }
}
