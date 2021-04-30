package com.xyz.cloud.sample.utils;

import com.xyz.cloud.sample.lock.LockTest;
import com.xyz.cloud.utils.AtomicLong;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.stream.IntStream;

@SpringBootTest
@Slf4j
@Disabled
public class AtomicLongTest {
    @Resource
    private RedissonClient redissonClient;
    private AtomicLong.CleanListener listener = (x) -> {
        log.info("{} is cleaned", x);
        throw new IllegalStateException("count out");
    };

    @Test
    public void case1() {
        AtomicLong atomicLong = new AtomicLong("test", "key", 0, 11, redissonClient, 1, listener);
        for (int i = 0; i < 10; i++) {
            int index = (int) atomicLong.incrementAndGet();
            log.info("current value: {}", index);
        }

        AtomicLong atomicLong1 = new AtomicLong("test", "key", 0, 11, redissonClient, 1, listener);
        LockTest.assertException(x -> atomicLong1.incrementAndGet(), IllegalStateException.class);
    }

    @Test
    public void case2() {
        AtomicLong atomicLong2 = new AtomicLong("test", "key", 0, 11, redissonClient, 1, listener);
        LockTest.assertNoException(x -> {
            IntStream.range(0, 10).parallel().forEach(t -> {
                int index = (int) atomicLong2.incrementAndGet();
                log.info("current value: {}", index);
            });
        });
    }
}
