package com.xyz.cloud.sample.retry;

import com.xyz.cloud.retry.RetryableException;
import com.xyz.cloud.retry.annotation.Retryable;
import com.xyz.cloud.trace.annotation.Traceable;
import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleEventListener {

    @Retryable(maxAttempts = 5)
    @EventListener
    @Async
    @Traceable
    public void handleTestEvent(SampleEvent event) {
        throw new RetryableException("Failed to handle sample event");
    }

    @EventListener
    @Async
    @Traceable
    public void nonRetryableEvent(SampleEvent event) {
        log.info("non-retryable sample event: {}", JsonUtils.beanToJson(event));
    }

    @Retryable(maxAttempts = 5)
    @EventListener
    @Traceable
    public void successfulEvent(SampleEvent event) {
        log.info("successful sample event: {}", JsonUtils.beanToJson(event));
    }
}
