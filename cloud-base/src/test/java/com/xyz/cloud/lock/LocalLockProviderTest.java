package com.xyz.cloud.lock;

import com.xyz.cloud.lock.provider.LocalLockProvider;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.exception.ValidationException;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class LocalLockProviderTest {

    @Test
    public void testLock() throws InterruptedException {
        LockProvider lockProvider = new LocalLockProvider();
        LockProvider.Lock lock = lockProvider.getLock("test");
        boolean locked = lock.tryLock(0, 10000);
        Assert.isTrue(locked && lock.isLocked(), "not locked");
        locked = lock.tryLock(0, 10000);
        Assert.isTrue(locked && lock.isLocked(), "not locked");
        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            boolean l = lock.tryLock(0, 10000);
            ValidationUtils.isTrue(!l, "locked");
            log.info("async lock result is {} as expected", l);
        });
        LockSupport.parkNanos(100);
        lock.unlock();
        TimeUnit.SECONDS.sleep(1);
    }
}
