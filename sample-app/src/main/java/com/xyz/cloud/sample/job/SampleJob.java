package com.xyz.cloud.sample.job;

import com.xyz.cloud.lock.annotation.Lock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleJob {
    @Lock(key = "'lock.event.repository.monitor'")
    @Scheduled(cron = "*/30 * * * * ?")
    public void run() {
        log.info("Sample job running");
    }

}
