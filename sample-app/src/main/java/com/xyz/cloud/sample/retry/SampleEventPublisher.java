package com.xyz.cloud.sample.retry;

import com.xyz.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleEventPublisher {
    private ApplicationEventPublisher publisher;

    public SampleEventPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publish(SampleEvent event) {
        log.info("publishing event {}", JsonUtils.beanToJson(event));
        publisher.publishEvent(event);
    }
}
