package com.xyz.cloud.lock;

import com.xyz.cloud.lock.annotation.Lock;
import com.xyz.cloud.lock.provider.LocalLockProvider;
import com.xyz.cloud.lock.provider.LockProvider;
import com.xyz.utils.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.LockSupport;

@Slf4j
public class LockAspectTest {
    @Test
    public void testLock() {
        LockAspectTest target = new LockAspectTest();
        AspectJProxyFactory factory = new AspectJProxyFactory(target);

        LockProvider lockProvider = new LocalLockProvider();
        LockAspect aspect = new LockAspect(lockProvider);
        factory.addAspect(aspect);
        LockAspectTest proxy = factory.getProxy();

        String name = "cloud-base";
        Assert.isTrue(name.equals(proxy.hello(name)), "hello is not properly executed");

        LockProvider.Lock lock = lockProvider.getLock(name);
        boolean locked = lock.tryLock(0, 20000L);
        ValidationUtils.isTrue(locked, "lock not obtained");

        CompletableFuture.runAsync(() -> {
            LockSupport.parkNanos(20);
            try{
                proxy.hello(name);
            } catch (Exception e) {
                log.error("hello failed", e);
            }
        });
 //       LockSupport.parkNanos(300);
        lock.unlock();
        Assert.isTrue(name.equals(proxy.hello(name)), "hello is not properly executed");
    }

    @Lock(key = "#name")
    public String hello(final String name) {
        log.info("hello {}", name);
        return name;
    }
}
