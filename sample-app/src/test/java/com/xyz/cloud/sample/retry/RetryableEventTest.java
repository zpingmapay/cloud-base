package com.xyz.cloud.sample.retry;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class RetryableEventTest {
    @Resource
    SampleEventPublisher eventPublisher;

    @Test
    public void testEvent() throws InterruptedException {
        SampleEvent event1 = new SampleEvent("Test1", "1 ");
        eventPublisher.publish(event1);
        SampleEvent event2 = new SampleEvent("Test2", "2");
        eventPublisher.publish(event2);
        SampleEvent event3 = new SampleEvent("Test3", "3");
        eventPublisher.publish(event3);
        TimeUnit.MINUTES.sleep(10);
    }
}
