package com.xyz.cloud.sample.lock;

import com.xyz.cloud.lock.annotation.Lock;
import com.xyz.cloud.lock.provider.RamLockProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class SampleService {
    @Lock(key = "'sample.'+#input")
    public void execute(String input) {
        log.info("sample service execute {}", input);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
